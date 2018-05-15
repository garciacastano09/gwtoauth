package jgc.asai.gwtoauth;

import jgc.asai.gwtoauth.client.BaseAppTest;
import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class BaseAppSuite extends GWTTestSuite {
  public static Test suite() {
    TestSuite suite = new TestSuite("Tests for BaseApp");
    suite.addTestSuite(BaseAppTest.class);
    return suite;
  }
}
