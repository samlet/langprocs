package com.samlet.langprocs;

import com.sagas.meta.model.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResourceProtoTests {
    @Test
    public void testResource() throws IOException {
        RsResource resource=RsResource.parseFrom(new FileInputStream("/pi/stack/data/resources/labels_res.data"));
        int total=resource.getPropertiesMap().size();
        System.out.println(String.format("total %d", total));
        RsProperty res=resource.getPropertiesOrThrow("AgreementType.description.PRODUCT_AGREEMENT");
        System.out.println(res.getValuesOrThrow("zh"));
    }

    @Test
    public void testLookups() throws IOException {
        RsLookups rs=RsLookups.parseFrom(new FileInputStream("/pi/stack/data/resources/labels_index.data"));
        int total=rs.getIndexTableCount();
        System.out.println(String.format("total %d", total));
        RsIndex index=rs.getIndexTableOrThrow("zh");
        RsStrings val=index.getIndexesOrThrow("产品");
        System.out.println(val);
    }

    @Test
    public void testFieldMappings() throws IOException {
        MetaMappingPackage pkg=MetaMappingPackage.parseFrom(new FileInputStream("/pi/stack/data/resources/form_res_sample.data"));
        MetaFieldMappings fieldMapping=pkg.getMappingsOrThrow("PartyLastName");
        fieldMapping.getFieldsList().forEach(fld->{
            System.out.println(String.format("%s:%s", fld.getFieldName(), fld.getFieldTitle()));
        });

    }
}
