//
// Copyright 2005, 2006, 2007 Xerox Corporation
// Leigh L. Klotz, Jr. <Leigh.Klotz@xerox.com>
//
// This software is licensed under Version 3.0 of the Academic Free License.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
// 

package com.xerox.adoc.dexss;

import java.io.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.ext.LexicalHandler;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;


/**
 * This class satisfies the @link DeXSSChangeListener interface and offers
 * a command-line utility for applying DeXSS to files.  It reports a possible
 * failure for any files that <em>don't</em> change.
 *
 * TODO: Do a better job of testing or expected removal and non-removal
 * of XSS code.
 */
public class Test implements DeXSSChangeListener {
  boolean changed = false;
  boolean showChanges = true;

  public void logXSSChange(String message) {
    if(showChanges)
      System.err.println("* " + message);
    changed = true;
  }

  public void logXSSChange(String message, String item1) {
    if(showChanges)
      System.err.println("* " + message + " " + item1);
    changed = true;
  }

  public void logXSSChange(String message, String item1, String item2) {
    if(showChanges)
      System.err.println("* " + message + " " + item1 + " " + item2);
    changed = true;
  }

  private boolean isChanged() {
    return changed;
  }

  private void resetChanged() {
    changed = false;
  }

  /**
   * This command-line test program processes the specified files or literals, and for each one
   * prints to System.out the following:
   * <ul>
   * <li>the file name (if any)</li>
   * <li>Any change messages from {@link DeXSSChangeListener}</li>
   * <li>Serialized XML result</li>
   * <li>A summary indicating whether the input changed or not (based on whether there were any XSSChangeListener messages)</li>
   * </ul>
   * TODO: A better test and regression harness.  More Test cases.
   * 
   * @param argv TemplatedPageCommand-line args are files to process, or if first arg is hypen, strings to process.
   */
  public static void main(String[] argv) throws IOException, SAXException {
    //OutputStreamWriter w = new OutputStreamWriter(System.out, "UTF-8");
		Pattern p_b = Pattern.compile("\\[b\\](.+?)\\[/b\\]");
		
		
		Matcher m = p_b.matcher("[b]asldjaslkdjasd[/b]asdasd[/b]");
		while (m.find()) {
			System.out.println(m.group()+" "+m.start()+" "+m.end());
		}

	  
	  /*
	  FileWriter w = new FileWriter("test.out");
	  
    Test test = new Test();
    
    String[] splitted = "/users/test1/index/test3.html/jcr:primaryType".split("/");
    StringBuffer sb = new StringBuffer();
    for (int i = 1; i<splitted.length-1; i++) {
    	if (i!=1) sb.append("/");
    	sb.append(splitted[i]);
    }
    System.out.println(sb.toString());
    

    System.out.println("ahsdhahsd\"asdasasd\"\"\"\"sdasdasd".replaceAll("\\\"", "&quot;"));
    DeXSS xss = DeXSS.createInstance(test, w);
	  String string = readFile("test.txt");
	  xss.process(string);
	  if (! test.isChanged()) {
	    // it might not have failed because the HTML parser might have cleaned it up for us
	  } else {
		  test.resetChanged();
	  }
	*/
  }
  
  private static String readFile(String file) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(file));
    StringWriter out = new StringWriter();
    int c;

    while ((c = in.read()) != -1)
      out.write(c);

    in.close();
    out.close();
    return out.toString();
  }
}
