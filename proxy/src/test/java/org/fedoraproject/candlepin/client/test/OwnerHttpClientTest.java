package org.fedoraproject.candlepin.client.test;

import static org.junit.Assert.*;

import java.util.List;

import org.fedoraproject.candlepin.client.test.ConsumerHttpClientTest.TestServletConfig;
import org.fedoraproject.candlepin.controller.Entitler;
import org.fedoraproject.candlepin.model.Consumer;
import org.fedoraproject.candlepin.model.ConsumerType;
import org.fedoraproject.candlepin.model.Entitlement;
import org.fedoraproject.candlepin.model.EntitlementPool;
import org.fedoraproject.candlepin.model.Owner;
import org.fedoraproject.candlepin.model.Product;
import org.fedoraproject.candlepin.test.TestDateUtil;
import org.fedoraproject.candlepin.test.TestUtil;
import org.junit.Before;
import org.junit.Test;

import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class OwnerHttpClientTest extends AbstractGuiceGrizzlyTest {

    private static final Long MAX_POOL_MEMBERS = new Long(10);
    private Owner owner;
    private Product product;
    private EntitlementPool entitlementPool;
    private Entitler entitler;
    private ConsumerType type;
    private Product anotherProduct;
    private EntitlementPool anotherEntitlementPool;

    @Before
    public void setUp() throws Exception {
        TestServletConfig.servletInjector = injector;
        startServer(TestServletConfig.class);
        
        owner = TestUtil.createOwner();
        ownerCurator.create(owner);
        
        type = new ConsumerType("some-consumer-type");
        consumerTypeCurator.create(type);
        
        product = TestUtil.createProduct();
        productCurator.create(product);
        
        anotherProduct = TestUtil.createProduct();
        productCurator.create(anotherProduct);
        
        entitlementPool = new EntitlementPool(owner, product, MAX_POOL_MEMBERS, 
                TestDateUtil.date(2010, 1, 1), TestDateUtil.date(2020, 12, 31));
        entitlementPoolCurator.create(entitlementPool);
        
        anotherEntitlementPool = new EntitlementPool(owner, anotherProduct, MAX_POOL_MEMBERS, 
                TestDateUtil.date(2010, 1, 1), TestDateUtil.date(2020, 12, 31));
        entitlementPoolCurator.create(anotherEntitlementPool);
        
        entitler = injector.getInstance(Entitler.class);
    }
    
    @Test
    public void listEntitlementsForExistingOwnerShouldPass() {
        for (int i = 0; i < MAX_POOL_MEMBERS.longValue()/2; i++) {
            Consumer c = TestUtil.createConsumer(type, owner);
            consumerCurator.create(c);
            entitler.createEntitlement(owner, c, product);
        }

        for (int i = 0; i < MAX_POOL_MEMBERS.longValue()/2; i++) {
            Consumer c = TestUtil.createConsumer(type, owner);
            consumerCurator.create(c);
            entitler.createEntitlement(owner, c, anotherProduct);
        }
        
        WebResource r = resource().path("/owner/" + owner.getId() + "/entitlement");
        List<Entitlement> returned = r.accept("application/json")
             .type("application/json")
             .get(new GenericType<List<Entitlement>>() {});
        
        assertEquals(MAX_POOL_MEMBERS, new Long(returned.size()));
    }
    
    @Test
    public void listEntitlementsForNonExistantOwnerShouldFail() {
        try {
            WebResource r = resource().path("/owner/1234/entitlement");
            r.accept("application/json")
                 .type("application/json")
                 .get(new GenericType<List<Entitlement>>() {});
        } catch (UniformInterfaceException e) {
            assertEquals(404, e.getResponse().getStatus());
        }
    }

}
