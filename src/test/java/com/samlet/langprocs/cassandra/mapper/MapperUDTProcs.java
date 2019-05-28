package com.samlet.langprocs.cassandra.mapper;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.utils.MoreObjects;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.samlet.langprocs.persist.CassandraMapper;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapperUDTProcs extends CassandraMapper {
    @Override
    public void createSchema(String ks) {
        session.execute(
                "CREATE KEYSPACE IF NOT EXISTS " + ks + " WITH replication "
                        + "= {'class':'SimpleStrategy', 'replication_factor':1};");
        session.execute("USE " + ks);
        execute(
                "CREATE TYPE IF NOT EXISTS address (street text, city text, \"ZIP_code\" int, phones set<text>)",
                "CREATE TABLE IF NOT EXISTS users (user_id uuid PRIMARY KEY, name text, mainaddress frozen<address>, other_addresses map<text,frozen<address>>)");

        execute(
                "CREATE TYPE IF NOT EXISTS \"Sub\"(i int)",
                "CREATE TABLE collection_examples (id int PRIMARY KEY, "
                        + "l list<frozen<\"Sub\">>, "
                        + "s set<frozen<\"Sub\">>, "
                        + "m1 map<int,frozen<\"Sub\">>, "
                        + "m2 map<frozen<\"Sub\">,int>, "
                        + "m3 map<frozen<\"Sub\">,frozen<\"Sub\">>)");
    }

    public void deleteObjects() {
        execute("DROP TABLE IF EXISTS users", "DROP TYPE  IF EXISTS address");
    }

    @Table(name = "users", readConsistency = "QUORUM", writeConsistency = "QUORUM")
    public static class User {
        @PartitionKey
        @Column(name = "user_id")
        private UUID userId;

        private String name;

        @Frozen
        private Address mainAddress;

        @Column(name = "other_addresses")
        @FrozenValue
        private Map<String, Address> otherAddresses = Maps.newHashMap();

        public User() {}

        public User(String name, Address address) {
            this.userId = UUIDs.random();
            this.name = name;
            this.mainAddress = address;
            this.otherAddresses = new HashMap<String, Address>();
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Address getMainAddress() {
            return mainAddress;
        }

        public void setMainAddress(Address address) {
            this.mainAddress = address;
        }

        public Map<String, Address> getOtherAddresses() {
            return otherAddresses;
        }

        public void setOtherAddresses(Map<String, Address> otherAddresses) {
            this.otherAddresses = otherAddresses;
        }

        public void addOtherAddress(String name, Address address) {
            this.otherAddresses.put(name, address);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other instanceof User) {
                User that = (User) other;
                return MoreObjects.equal(this.userId, that.userId)
                        && MoreObjects.equal(this.name, that.name)
                        && MoreObjects.equal(this.mainAddress, that.mainAddress)
                        && MoreObjects.equal(this.otherAddresses, that.otherAddresses);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return MoreObjects.hashCode(this.userId, this.name, this.mainAddress, this.otherAddresses);
        }

        @Override
        public String toString() {
            return String.format(
                    "User(userId=%s, name=%s, mainAddress=%s, otherAddresses=%s)",
                    userId, name, mainAddress, otherAddresses);
        }
    }

    @UDT(name = "address")
    public static class Address {

        // Dummy constant to test that static fields are properly ignored
        public static final int FOO = 1;

        @Field // not strictly required, but we want to check that the annotation works without a name
        private String city;

        // Declared out of order compared to the UDT definition, to make sure that we serialize fields
        // in the correct order (JAVA-884)
        private String street;

        @Field(name = "ZIP_code", caseSensitive = true)
        private int zipCode;

        private Set<String> phones;

        public Address() {}

        public Address(String street, String city, int zipCode, String... phones) {
            this.street = street;
            this.city = city;
            this.zipCode = zipCode;
            this.phones = new HashSet<String>();
            Collections.addAll(this.phones, phones);
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public int getZipCode() {
            return zipCode;
        }

        public void setZipCode(int zipCode) {
            this.zipCode = zipCode;
        }

        public Set<String> getPhones() {
            return phones;
        }

        public void setPhones(Set<String> phones) {
            this.phones = phones;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other instanceof Address) {
                Address that = (Address) other;
                return MoreObjects.equal(this.street, that.street)
                        && MoreObjects.equal(this.city, that.city)
                        && MoreObjects.equal(this.zipCode, that.zipCode)
                        && MoreObjects.equal(this.phones, that.phones);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return MoreObjects.hashCode(this.street, this.city, this.zipCode, this.phones);
        }

        @Override
        public String toString() {
            return String.format(
                    "Address(street=%s, city=%s, zip=%d, phones=%s)", street, city, zipCode, phones);
        }
    }

    @Accessor
    public interface UserAccessor {
        @Query("SELECT * FROM users WHERE user_id=:userId")
        User getOne(@Param("userId") UUID userId);

        @Query("UPDATE users SET other_addresses[:name]=:address WHERE user_id=:id")
        ResultSet addAddress(
                @Param("id") UUID id, @Param("name") String addressName, @Param("address") Address address);

        @Query("UPDATE users SET other_addresses=:addresses where user_id=:id")
        ResultSet setOtherAddresses(
                @Param("id") UUID id, @Param("addresses") Map<String, Address> addresses);

        @Query("SELECT * FROM users")
        Result<User> getAll();
    }

    @UDT(name = "Sub", caseSensitiveType = true)
    public static class Sub {
        private int i;

        public Sub() {}

        public Sub(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Sub) {
                Sub that = (Sub) other;
                return this.i == that.i;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return MoreObjects.hashCode(i);
        }
    }

    @Table(name = "collection_examples")
    public static class CollectionExamples {
        @PartitionKey private int id;

        @FrozenValue private List<Sub> l;

        @FrozenValue private Set<Sub> s;

        @FrozenValue private Map<Integer, Sub> m1;

        @FrozenKey private Map<Sub, Integer> m2;

        @FrozenKey @FrozenValue private Map<Sub, Sub> m3;

        public CollectionExamples() {}

        public CollectionExamples(int id, int value) {
            this.id = id;
            // Just fill the collections with random values
            Sub sub1 = new Sub(value);
            Sub sub2 = new Sub(value + 1);
            this.l = Lists.newArrayList(sub1, sub2);
            this.s = Sets.newHashSet(sub1, sub2);
            this.m1 = ImmutableMap.of(1, sub1, 2, sub2);
            this.m2 = ImmutableMap.of(sub1, 1, sub2, 2);
            this.m3 = ImmutableMap.of(sub1, sub1, sub2, sub2);
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public List<Sub> getL() {
            return l;
        }

        public void setL(List<Sub> l) {
            this.l = l;
        }

        public Set<Sub> getS() {
            return s;
        }

        public void setS(Set<Sub> s) {
            this.s = s;
        }

        public Map<Integer, Sub> getM1() {
            return m1;
        }

        public void setM1(Map<Integer, Sub> m1) {
            this.m1 = m1;
        }

        public Map<Sub, Integer> getM2() {
            return m2;
        }

        public void setM2(Map<Sub, Integer> m2) {
            this.m2 = m2;
        }

        public Map<Sub, Sub> getM3() {
            return m3;
        }

        public void setM3(Map<Sub, Sub> m3) {
            this.m3 = m3;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof CollectionExamples) {
                CollectionExamples that = (CollectionExamples) other;
                return MoreObjects.equal(this.id, that.id)
                        && MoreObjects.equal(this.l, that.l)
                        && MoreObjects.equal(this.s, that.s)
                        && MoreObjects.equal(this.m1, that.m1)
                        && MoreObjects.equal(this.m2, that.m2)
                        && MoreObjects.equal(this.m3, that.m3);
            }
            return false;
        }
    }


    public void testSimpleEntity() throws Exception {
        Mapper<User> m = new MappingManager(session).mapper(User.class);

        User u1 =
                new User(
                        "Paul", new Address("12 4th Street", "Springfield", 12345, "12341343", "435423245"));
        u1.addOtherAddress("work", new Address("5 Main Street", "Springfield", 12345, "23431342"));
        m.save(u1);

        assertEquals(m.get(u1.getUserId()), u1);
    }
    public void testAccessor() throws Exception {
        MappingManager manager = new MappingManager(session);

        Mapper<User> m = new MappingManager(session).mapper(User.class);
        User u1 =
                new User(
                        "Paul", new Address("12 4th Street", "Springfield", 12345, "12341343", "435423245"));
        m.save(u1);

        UserAccessor userAccessor = manager.createAccessor(UserAccessor.class);

        Address workAddress = new Address("5 Main Street", "Springfield", 12345, "23431342");
        userAccessor.addAddress(u1.getUserId(), "work", workAddress);

        User u2 = userAccessor.getOne(u1.getUserId());
        assertEquals(workAddress, u2.getOtherAddresses().get("work"));

        // Adding a null value should remove it from the list.
        userAccessor.addAddress(u1.getUserId(), "work", null);
        User u3 = userAccessor.getOne(u1.getUserId());
        // assertThat(u3.getOtherAddresses()).doesNotContainKey("work");
        System.out.println(u3.getOtherAddresses());

        // Add a bunch of other addresses
        Map<String, Address> otherAddresses = Maps.newHashMap();
        otherAddresses.put("work", workAddress);
        otherAddresses.put(
                "cabin", new Address("42 Middle of Nowhere", "Lake of the Woods", 49553, "8675309"));

        userAccessor.setOtherAddresses(u1.getUserId(), otherAddresses);
        User u4 = userAccessor.getOne(u1.getUserId());
        // assertThat(u4.getOtherAddresses()).isEqualTo(otherAddresses);
        System.out.println(u4.getOtherAddresses());

        // Nullify other addresses
        userAccessor.setOtherAddresses(u1.getUserId(), null);
        User u5 = userAccessor.getOne(u1.getUserId());
        // assertThat(u5.getOtherAddresses()).isEmpty();
        System.out.println(u5.getOtherAddresses());

        // No argument call
        Result<User> u = userAccessor.getAll();
        // assertEquals(u.one(), u5);
        System.out.println(u.one());
        // assertTrue(u.isExhausted());
        System.out.println(u.isExhausted());
    }

    public void testCollections() throws Exception {
        Mapper<CollectionExamples> m = new MappingManager(session).mapper(CollectionExamples.class);

        CollectionExamples c = new CollectionExamples(1, 1);
        m.save(c);

        assertEquals(m.get(c.getId()), c);
    }

    static String[] CONTACT_POINTS = {"127.0.0.1"};
    static int PORT = 9042;

    public static void main(String[] args) {

        MapperUDTProcs procs = new MapperUDTProcs();
        try {
            procs.connect(CONTACT_POINTS, PORT);
            String ks = "contacts";
            procs.createSchema(ks);
            procs.testSimpleEntity();
            procs.testAccessor();

            procs.testCollections();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            procs.close();
        }
    }
}
