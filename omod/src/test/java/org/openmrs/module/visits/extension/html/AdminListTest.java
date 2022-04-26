package org.openmrs.module.visits.extension.html;

import org.junit.Test;
import org.openmrs.module.Extension;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AdminListTest {

  private final AdminList adminList = new AdminList();

  @Test
  public void shouldReturnCorrectLinks() {
    Map<String, String> actual = adminList.getLinks();

    assertNotNull(actual);
    assertEquals(1, actual.size());
    assertEquals(
        "Visits module REST API", actual.get("/ms/uiframework/resource/visits/swagger/index.html"));
  }

  @Test
  public void shouldReturnCorrectModuleTitle() {
    String actual = adminList.getTitle();

    assertNotNull(actual);
    assertEquals("Visits module", actual);
  }

  @Test
  public void shouldReturnCorrectMediaType() {
    Extension.MEDIA_TYPE actual = adminList.getMediaType();

    assertNotNull(actual);
    assertEquals(Extension.MEDIA_TYPE.html, actual);
  }
}
