/*
 *  Copyright 2010 Robert Csakany <robson@semmi.se>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.liveSense.service.xssRemove;

import javax.jcr.RepositoryException;

/**
 *
 * @author Robert Csakany (robson@semmi.se)
 * @created Feb 14, 2010
 */
public interface XSSRemove {
    public void removeXSSsecurityVulnerability(String parentFolder, String fileName) throws RepositoryException, Exception;
}
