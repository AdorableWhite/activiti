package com.zxx.activitiflow.processEngine.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.interceptor.CommandExecutor;
import org.activiti.engine.impl.persistence.deploy.ProcessDefinitionInfoCacheObject;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionInfoEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionInfoEntityManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProcessDefinitionInfoCache {
    protected Map<String, ProcessDefinitionInfoCacheObject> cache;
    protected CommandExecutor commandExecutor;

    public ProcessDefinitionInfoCache(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        this.cache = new HashMap<String, ProcessDefinitionInfoCacheObject>();
    }

    public ProcessDefinitionInfoCache(CommandExecutor commandExecutor, final int limit) {
        this.commandExecutor = commandExecutor;
        this.cache = Collections.synchronizedMap(new LinkedHashMap<String, ProcessDefinitionInfoCacheObject>(limit + 1, 0.75f, true) {
            private static final long serialVersionUID = 1L;
            protected boolean removeEldestEntry(Map.Entry<String, ProcessDefinitionInfoCacheObject> eldest) {
                boolean removeEldest = size() > limit;
                return removeEldest;
            }
        });
    }

    public ProcessDefinitionInfoCacheObject get(final String processDefinitionId) {
        ProcessDefinitionInfoCacheObject infoCacheObject = null;
        if (cache.containsKey(processDefinitionId)) {
            infoCacheObject = commandExecutor.execute(new Command<ProcessDefinitionInfoCacheObject>() {
                public ProcessDefinitionInfoCacheObject execute(CommandContext commandContext) {
                    ProcessDefinitionInfoEntityManager infoEntityManager = commandContext.getProcessDefinitionInfoEntityManager();
                    ObjectMapper objectMapper = commandContext.getProcessEngineConfiguration().getObjectMapper();
                    ProcessDefinitionInfoCacheObject cacheObject = cache.get(processDefinitionId);
                    ProcessDefinitionInfoEntity infoEntity = infoEntityManager.findProcessDefinitionInfoByProcessDefinitionId(processDefinitionId);
                    if (infoEntity != null && infoEntity.getRevision() != cacheObject.getRevision()) {
                        cacheObject.setRevision(infoEntity.getRevision());
                        if (infoEntity.getInfoJsonId() != null) {
                            byte[] infoBytes = infoEntityManager.findInfoJsonById(infoEntity.getInfoJsonId());
                            try {
                                ObjectNode infoNode = (ObjectNode) objectMapper.readTree(infoBytes);
                                cacheObject.setInfoNode(infoNode);
                            } catch (Exception e) {
                            }
                        }
                    } else if (infoEntity == null) {
                        cacheObject.setRevision(0);
                        cacheObject.setInfoNode(objectMapper.createObjectNode());
                    }
                    return cacheObject;
                }
            });
        }
        return infoCacheObject;

    }
}