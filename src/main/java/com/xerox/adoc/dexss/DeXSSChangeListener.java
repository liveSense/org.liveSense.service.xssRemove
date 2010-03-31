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

/**
 * Objects implementing this interface are suitable for Property {@link DeXSSFilterPipeline#DeXSS_CHANGE_LISTENER}.
 * Useful mostly for debugging, or to log XSS events.
 * 
 * TODO: An upgrade that accepts a SAX2 Location would be nice.
 */
public interface DeXSSChangeListener {
  /**
   * Called when a change happens but there is no other information.
   * @param message Main message
   */
  public void logXSSChange(String message);

  /**
   * Called when a change happens and there is one other informational item.
   * @param message Main message
   * @param item1 Information item
   */
  public void logXSSChange(String message, String item1);

  /**
   * Called when a change happens and there are two informational items.
   * @param message Main message
   * @param item1 Information item 1
   * @param item2 Information item 2
   */
  public void logXSSChange(String message, String item1, String item2);
}
