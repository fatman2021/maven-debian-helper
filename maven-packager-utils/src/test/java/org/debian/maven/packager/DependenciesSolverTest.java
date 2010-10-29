/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.debian.maven.packager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.debian.maven.repo.DependencyRule;
import org.debian.maven.repo.ListOfPOMs;
import org.debian.maven.repo.Repository;

/**
 *
 * @author ludo
 */
public class DependenciesSolverTest extends TestCase {

    private File testDir = new File("tmp");
    private File pomFile = new File(testDir, "pom.xml");
    private List openedReaders = new ArrayList();

    protected void setUp() throws Exception {
        super.setUp();
        testDir.mkdirs();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        for (Iterator i = openedReaders.iterator(); i.hasNext(); ) {
            Reader reader = (Reader) i.next();
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        openedReaders.clear();
        FileUtils.deleteDirectory(testDir);
    }

    /**
     * Test of solveDependencies method, of class DependenciesSolver.
     */
    public void testSolvePlexusActiveCollectionsDependencies() throws Exception {
        useFile("plexus-active-collections/pom.xml", pomFile);
        DependenciesSolver solver = new DependenciesSolver();
        solver.setMavenRepo(getFileInClasspath("repository/root.dir").getParentFile());
        solver.setOutputDirectory(testDir);
        solver.setExploreProjects(true);
        solver.setPackageName("libplexus-active-collections-java");
        solver.setPackageType("maven");
        File listOfPoms = getFileInClasspath("libplexus-active-collections-java.poms");
        solver.setBaseDir(getFileInClasspath("plexus-active-collections/pom.xml").getParentFile());
        solver.setListOfPoms(new File(listOfPoms.getParent(), listOfPoms.getName()));
        solver.setInteractive(false);
        solver.setOffline(true);

        solver.solveDependencies();

        assertTrue("Did not expect any issues", solver.getIssues().isEmpty());

        solver.setBaseDir(testDir);
        solver.setListOfPoms(new File(testDir, "libplexus-active-collections-java.poms"));

        solver.saveListOfPoms();
        solver.saveMavenRules();
        solver.saveSubstvars();

        assertFileEquals("libplexus-active-collections-java.poms", "libplexus-active-collections-java.poms");
        assertFileEquals("libplexus-active-collections-java.substvars", "libplexus-active-collections-java.substvars");
        assertFileEquals("libplexus-active-collections-java.rules", "maven.rules");
    }

    /**
     * Test of solveDependencies method, of class DependenciesSolver.
     */
    public void testSolvePlexusUtils2Dependencies() throws Exception {
        useFile("plexus-utils2/pom.xml", pomFile);
        DependenciesSolver solver = new DependenciesSolver();
        solver.setMavenRepo(getFileInClasspath("repository/root.dir").getParentFile());
        solver.setOutputDirectory(testDir);
        solver.setExploreProjects(true);
        solver.setPackageName("libplexus-utils2-java");
        solver.setPackageType("maven");
        solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.apache.maven.plugins maven-release-plugin * *"));
        File listOfPoms = getFileInClasspath("libplexus-utils2-java.poms");
        solver.setBaseDir(getFileInClasspath("plexus-utils2/pom.xml").getParentFile());
        solver.setListOfPoms(new File(listOfPoms.getParent(), listOfPoms.getName()));
        solver.setInteractive(false);
        solver.setOffline(true);

        solver.solveDependencies();

        assertTrue("Did not expect any issues", solver.getIssues().isEmpty());

        solver.setBaseDir(testDir);
        solver.setListOfPoms(new File(testDir, "libplexus-utils2-java.poms"));

        solver.saveListOfPoms();
        solver.saveMavenRules();
        solver.saveSubstvars();

        assertFileEquals("libplexus-utils2-java.poms", "libplexus-utils2-java.poms");
        assertFileEquals("libplexus-utils2-java.substvars", "libplexus-utils2-java.substvars");
        assertFileEquals("libplexus-utils2-java.rules", "maven.rules");
    }

    /**
     * Test of solveDependencies method, of class DependenciesSolver.
     */
    public void testSolveOpenMRSDependenciesWithErrors() throws Exception {
        useFile("openmrs/pom.xml", pomFile);
        DependenciesSolver solver = new DependenciesSolver();
        solver.setMavenRepo(getFileInClasspath("repository/root.dir").getParentFile());
        solver.setOutputDirectory(testDir);
        solver.setExploreProjects(false);
        solver.setPackageName("openmrs");
        solver.setPackageType("maven");
        //solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.apache.maven.plugins maven-release-plugin * *"));
        File listOfPoms = getFileInClasspath("openmrs.poms");
        solver.setBaseDir(getFileInClasspath("openmrs/pom.xml").getParentFile());
        solver.setListOfPoms(new File(listOfPoms.getParent(), listOfPoms.getName()));
        solver.setInteractive(false);
        solver.setOffline(true);

        solver.solveDependencies();

        assertEquals(1, solver.getIssues().size());
        assertTrue(solver.getIssues().get(0).toString().indexOf("buildnumber-maven-plugin") > 0);
    }

    public void testSolveOpenMRSDependencies() throws Exception {
        useFile("openmrs/pom.xml", pomFile);
        DependenciesSolver solver = new DependenciesSolver();
        solver.setMavenRepo(getFileInClasspath("repository/root.dir").getParentFile());
        solver.setOutputDirectory(testDir);
        solver.setExploreProjects(false);
        solver.setPackageName("openmrs");
        solver.setPackageType("maven");
        solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.openmrs.codehaus.mojo buildnumber-maven-plugin * *"));
        solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.codehaus.mojo build-helper-maven-plugin * *"));
        solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.apache.maven.plugins maven-assembly-plugin * *"));
        File listOfPoms = getFileInClasspath("openmrs.poms");
        solver.setBaseDir(getFileInClasspath("openmrs/pom.xml").getParentFile());
        solver.setListOfPoms(new File(listOfPoms.getParent(), listOfPoms.getName()));
        solver.setInteractive(false);
        solver.setOffline(true);

        solver.solveDependencies();

        assertTrue("Did not expect any issues", solver.getIssues().isEmpty());

        solver.setBaseDir(testDir);
        solver.setListOfPoms(new File(testDir, "openmrs.poms"));

        solver.saveListOfPoms();
        solver.saveMavenRules();
        solver.saveSubstvars();

        assertFileEquals("openmrs.poms", "openmrs.poms");
        assertFileEquals("openmrs.substvars", "openmrs.substvars");
        assertFileEquals("openmrs.rules", "maven.rules");
    }

    public void testSolveOpenMRSApiDependencies() throws Exception {
        useFile("openmrs/pom.xml", pomFile);
        DependenciesSolver solver = new DependenciesSolver();
        solver.setMavenRepo(getFileInClasspath("repository/root.dir").getParentFile());
        solver.setOutputDirectory(testDir);
        solver.setExploreProjects(false);
        solver.setPackageName("openmrs");
        solver.setPackageType("maven");
        solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.openmrs.codehaus.mojo buildnumber-maven-plugin * *"));
        solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.codehaus.mojo build-helper-maven-plugin * *"));
        solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.apache.maven.plugins maven-assembly-plugin * *"));
        solver.getPomTransformer().addIgnoreRule(new DependencyRule("org.springframework * * *"));
        File listOfPoms = getFileInClasspath("openmrs-api.poms");
        solver.setBaseDir(getFileInClasspath("openmrs/pom.xml").getParentFile());
        solver.setListOfPoms(new File(listOfPoms.getParent(), listOfPoms.getName()));
        solver.setInteractive(false);
        solver.setOffline(true);

        solver.solveDependencies();

        assertTrue("Did not expect any issues", solver.getIssues().isEmpty());

        solver.setBaseDir(testDir);
        solver.setListOfPoms(new File(testDir, "openmrs.poms"));

        solver.saveListOfPoms();
        solver.saveMavenRules();
        solver.saveSubstvars();

        assertFileEquals("openmrs.poms", "openmrs.poms");
        assertFileEquals("openmrs.substvars", "openmrs.substvars");
        assertFileEquals("openmrs.rules", "maven.rules");
    }

    protected void assertFileEquals(String resource, String fileName) throws Exception {
        File file = new File(testDir, fileName);
        assertTrue(file.exists());
        LineNumberReader fileReader = new LineNumberReader(new FileReader(file));
        LineNumberReader refReader = new LineNumberReader(read(resource));

        String ref, test = null;
        boolean skipReadTest = false;
        while (true) {
            if (!skipReadTest) {
                test = fileReader.readLine();

                if (test != null && (test.startsWith("#") || test.trim().isEmpty())) {
                    continue;
                }
            }
            skipReadTest = false;

            ref = refReader.readLine();
            if (ref == null) {
                return;
            }
            if (ref.startsWith("#") || ref.trim().isEmpty()) {
                skipReadTest = true;
                continue;
            }
            assertEquals("Error in " + fileName, ref.trim(), test.trim());
        }
    }

    protected void useFile(String resource, File file) throws IOException {
        final FileWriter out = new FileWriter(file);
        final Reader in = read(resource);
        IOUtils.copy(in,out);
        in.close();
        out.close();
    }

    protected Reader read(String resource) {
        Reader r = new InputStreamReader(this.getClass().getResourceAsStream("/" + resource));
        openedReaders.add(r);
        return r;
    }

    protected File getFileInClasspath(String resource) {
        if (! resource.startsWith("/")) {
            resource = "/" + resource;
        }
        URL url = this.getClass().getResource(resource);
        File f;
        try {
          f = new File(url.toURI());
        } catch(URISyntaxException e) {
          f = new File(url.getPath());
        }
        return f;
    }

}
