package com.samlet.langprocs.delegator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.*;

import java.net.MalformedURLException;
import java.net.URL;

public class OdooTests {
    public static void main(String[] args) throws MalformedURLException, XmlRpcException {
        System.out.println("end.");
        final String url ="http://localhost:8069",
                db = "odoo12",
                username = "samlet@163.com",
                password = "intelibm";
        final XmlRpcClient client = new XmlRpcClient();
        final XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
        common_config.setServerURL(
                new URL(String.format("%s/xmlrpc/2/common", url)));
        Object result=client.execute(common_config, "version", Lists.newArrayList());
        System.out.println(result);

        //int uid = (int)client.execute(
        //        common_config, "authenticate", Lists.asList(
        //                db, username, password));
    }
}
