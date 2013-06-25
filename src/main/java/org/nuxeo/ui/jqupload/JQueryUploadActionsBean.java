/*
 * (C) Copyright ${year} Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     <a href="mailto:tdelprat@nuxeo.com">Tiry</a>
 */

package org.nuxeo.ui.jqupload;

import java.io.Serializable;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("jqFileUploadActions")
@Scope(ScopeType.EVENT)
public class JQueryUploadActionsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(JQueryUploadActionsBean.class);

    protected static final String JQUERYUPLOAD_RESOURCES_MARKER = "JQUERYUPLOAD_RESOURCES_MARKER";

    protected String batchId;

    public boolean mustIncludeResources() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();

            if (request.getAttribute(JQUERYUPLOAD_RESOURCES_MARKER) != null) {
                return false;
            } else {
                request.setAttribute(JQUERYUPLOAD_RESOURCES_MARKER, "done");
                return true;
            }
        }
        return false;
    }

    public String getBatchId() {
        return UUID.randomUUID().toString();
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }




}
