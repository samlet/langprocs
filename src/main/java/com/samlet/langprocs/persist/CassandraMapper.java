package com.samlet.langprocs.persist;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.google.api.client.util.Throwables;
import org.slf4j.*;

import java.util.Arrays;
import java.util.Collection;

public abstract class CassandraMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraMapper.class);
    protected Session session;
    protected Cluster cluster;

    /** Closes the session and the cluster. */
    public void close() {
        session.close();
        cluster.close();
    }

    /**
     * Initiates a connection to the cluster specified by the given contact point.
     *
     * @param contactPoints the contact points to use.
     * @param port the port to use.
     */
    public void connect(String[] contactPoints, int port) {

        cluster = Cluster.builder().addContactPoints(contactPoints).withPort(port).build();

        System.out.printf("Connected to cluster: %s%n", cluster.getMetadata().getClusterName());

        session = cluster.connect();
    }
    /**
     * Executes the given statements with the test's session object.
     *
     * <p>Useful to create test fixtures and/or load data before tests.
     *
     * <p>This method should not be called if a session object hasn't been created (if CCM
     * configuration specifies {@code createSession = false})
     *
     * @param statements The statements to execute.
     */
    public void execute(Collection<String> statements) {
        assert session != null;
        for (String stmt : statements) {
            try {
                session.execute(stmt);
            } catch (Exception e) {
                // errorOut();
                LOGGER.error("Could not execute statement: " + stmt, e);
                Throwables.propagate(e);
            }
        }
    }
    public void execute(String... statements) {
        execute(Arrays.asList(statements));
    }

    public abstract void createSchema(String ks);
}
