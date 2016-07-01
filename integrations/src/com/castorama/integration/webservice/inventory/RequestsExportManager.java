package com.castorama.integration.webservice.inventory;


import atg.nucleus.GenericService;
import atg.repository.Repository;

import javax.servlet.Servlet;

/**
 * @author EPAM team
 */
public class RequestsExportManager extends GenericService {

    private Repository webServicesLogRepository;
    private String rootDirectoryPath;
    private String exportFilePrefix;

    protected Servlet createAdminServlet() {
        return new RequestExportAdminServlet(this, getNucleus());
    }

    public Repository getWebServicesLogRepository() {
        return webServicesLogRepository;
    }

    public void setWebServicesLogRepository(Repository webServicesLogRepository) {
        this.webServicesLogRepository = webServicesLogRepository;
    }

    public String getRootDirectoryPath() {
        return rootDirectoryPath;
    }

    public void setRootDirectoryPath(String rootDirectoryPath) {
        this.rootDirectoryPath = rootDirectoryPath;
    }

    public String getExportFilePrefix() {
        return exportFilePrefix;
    }

    public void setExportFilePrefix(String exportFilePrefix) {
        this.exportFilePrefix = exportFilePrefix;
    }
}
