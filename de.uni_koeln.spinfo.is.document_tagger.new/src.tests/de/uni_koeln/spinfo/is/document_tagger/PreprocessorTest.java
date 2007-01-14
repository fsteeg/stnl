package de.uni_koeln.spinfo.is.document_tagger;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class PreprocessorTest {
    @Test
    public void testPreproc1() {
        Preprocessor p = new Preprocessor(
                "http://blog.lostlake.org/index.php?/archives/27-5-Things-a-Ruby-developer-needs-to-know-about-Scala.html");
        String clean = p.clean();
        System.out.println(clean);
    }

    @Test
    public void testPreproc2() {
        Preprocessor p = new Preprocessor("http://www.digg.com");
        String clean = p.clean();
        System.out.println(clean);
    }

    @Test
    public void testWordListFiltering() {

        filterTest(Arrays.asList("a", "interesting", "paradigm"), Arrays
                .asList("interesting", "paradigm"));

        filterTest(Arrays.asList("A", "interesting", "paradigm", "i"), Arrays
                .asList("interesting", "paradigm"));

    }

    private void filterTest(List<String> a, List<String> b) {
        Set<String> filtered0 = Preprocessor.filter(new HashSet<String>(a),
                "stopwords");
        assertEquals(new HashSet<String>(b), filtered0);
    }
}
