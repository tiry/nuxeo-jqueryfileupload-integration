/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 */
package org.nuxeo.ui.jqupload;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.util.BlobList;
import org.nuxeo.ecm.automation.jsf.OperationHelper;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.filemanager.api.FileManager;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

/**
 * Use {@link FileManager} to create documents from blobs
 *
 * @author Tiry (tdelprat@nuxeo.com)
 *
 */
@Operation(id = CDFileManagerImport2.ID, category = Constants.CAT_SERVICES, label = "Create Document from file", description = "Create Document(s) from Blob(s) using the FileManagerService.")
public class CDFileManagerImport2 {

    public static final String ID = "CDFileManager.Import2";

    @Context
    protected CoreSession session;

    @Context
    protected FileManager fileManager;

    @Context
    protected AutomationService as;

    @Context
    protected OperationContext context;

    @Param(name = "docType", required = false)
    protected String docType = null;

    @Param(name = "overwite", required = false)
    protected Boolean overwite = false;

    protected DocumentModel getCurrentDocument() throws Exception {
        String cdRef = (String) context.get("currentDocument");
        return as.getAdaptedValue(context, cdRef, DocumentModel.class);
    }

    @OperationMethod
    public DocumentModel run(Blob blob) throws Exception {
        DocumentModel currentDocument = getCurrentDocument();

        // TODO : add here custom logic
        // Ideally all validation logic should already be inside the underlying service ...
        DocumentModel doc =  fileManager.createDocumentFromBlob(session, blob, currentDocument.getPathAsString(),overwite , blob.getFilename());
        addFacesMessage("document_saved", doc.getType());
        sendFlushEvents();
        return doc;
    }

    @SuppressWarnings("deprecation")
    protected void addFacesMessage(String tmpl, String type) {
        ResourcesAccessor resourcesAccessor = (ResourcesAccessor) Contexts.lookupInStatefulContexts("resourcesAccessor");
        FacesMessages facesMessages = (FacesMessages) Contexts.getConversationContext().get("org.jboss.seam.international.statusMessages");
        facesMessages.add(
                StatusMessage.Severity.INFO,
                resourcesAccessor.getMessages().get(tmpl), resourcesAccessor.getMessages().get(type));
    }

    protected void sendFlushEvents() {
        NavigationContext context = OperationHelper.getNavigationContext();
        DocumentModel dm = context.getCurrentDocument();
        if (dm != null) {
            Events.instance().raiseEvent(EventNames.DOCUMENT_CHANGED, dm);
            Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
                    dm);
        }
    }

    @OperationMethod
    public DocumentModelList run(BlobList blobs) throws Exception {
        DocumentModelList result = new DocumentModelListImpl();
        for (Blob blob : blobs) {
            result.add(run(blob));
        }
        return result;
    }

}
