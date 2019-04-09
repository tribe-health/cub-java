package com.ivelum.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;
import com.ivelum.exception.NotFoundException;
import com.ivelum.net.ApiResource;
import com.ivelum.net.Params;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class OrganizationTest extends CubModelBaseTest {

  @Test
  public void testRead() throws CubException, UnsupportedEncodingException {
    String baseUrl = Cub.baseUrl;
    Cub.baseUrl = String.format("http://127.0.0.1:%s/", wireMockRule.port());
    String objUrl = String.format(
        "/%s%s",
        Cub.version,
        ApiResource.getInstanceUrl(ApiResource.getInstanceName(Organization.class), "org_123"
    ));


    setMock(objUrl, "organization", 200, Cub.apiKey);
    Organization org = Organization.get("org_123");
    Cub.baseUrl = baseUrl;


    assertEquals("org_123", org.id);
    assertEquals("name", org.name);
    assertFalse(org.moderatorApproved);
    assertNotNull(org.created);
    assertNotNull(org.modified);
    assertEquals(1, org.tags.size());
    assertTrue(org.tags.contains("EMS"));
    assertEquals("website", org.website);
    assertFalse(org.state.isExpanded());
    assertEquals("stt_123", org.state.getId());
    assertEquals("phone", org.phone);
    assertEquals("fax", org.fax);
    assertEquals("employees", org.employees);
    assertEquals("email", org.email);
    assertEquals("county", org.county);
    assertEquals("city", org.city);
    assertEquals("logo", org.logo);
    assertEquals("address", org.address);
    assertEquals("hrPhone", org.hrPhone);
    assertEquals("postalCode", org.postalCode);
    assertFalse(org.country.isExpanded());
    assertEquals("cry_123", org.country.getId());
    assertNull(org.deleted);
  }

  @Test
  public void testDeserializationDeletedWebhook() throws DeserializationException {
    String orgJson = getFixture("organization_deleted");

    Organization org = (Organization) Cub.factory.fromString(orgJson);
    assertTrue(org.deleted);
  }

  @Test
  public void testNotFound() throws UnsupportedEncodingException {
    try {
      Organization.get("non_exists_id");
      // exception expected
      fail();
    } catch (NotFoundException e) {
      assertTrue(e.getMessage().contains("Organization id=non_exists_id not found"));
    } catch (CubException e) {
      e.printStackTrace();
      fail(); // unexpected
    }
  }


  @Test
  public void testStateExpandableIdOnly() throws CubException, UnsupportedEncodingException {
    List<CubObject> organizations = Organization.list();
    String orgId = organizations.get(0).id;
    Params params = new Params();
    params.setExpands("state", "country", "state__country");
    Organization org = Organization.get(orgId, params);
    assertNotNull(org.state);
    assertNotNull(org.state.getId());
    assertNotNull(org.state.getExpanded());

    State state = org.state.getExpanded();

    assertNotNull(state.country);
    assertNotNull(state.country.getId());
    assertNotNull(state.country.getExpanded());

    assertNotNull(org.country);
    assertNotNull(org.country.getId());
    assertNotNull(org.country.getExpanded());
  }

  @Test
  public void testListOrganizations() throws CubException, UnsupportedEncodingException {
    List<CubObject> organizations = Organization.list();
    List<String> ids = new LinkedList<>();
    for (CubObject item : organizations) {
      ids.add(((Organization) item).id);
    }

    Params params = new Params();
    params.setOffset(20).setCount(5);
    List<CubObject> anotherPage = Organization.list(params);

    assertEquals(5, anotherPage.size());

    for (CubObject item : anotherPage) {
      assertFalse(ids.contains(((Organization) item).id));
    }
  }
}
