/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2021 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2021 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.brannock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Brannock implements BundleActivator {
    private MBeanServer m_mbeanServer;
    private DataSource m_jdbcDS;
    private JSONObject m_jsOut;
    private JSONObject m_jsJmxData;
    private JSONObject m_jsJdbcData;
    private JSONObject m_jsDerivedData;
    private Long m_vmUptime, m_pollerTasksCompleted, m_enqueuedOps;
    private Double m_newtsSamplesInsertedMeanRate, m_eventLogsProcessMeanRate, m_flowsPersistedMeanRate;
    
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        m_mbeanServer = ManagementFactory.getPlatformMBeanServer();
        m_jdbcDS = getJdbcDataSource(bundleContext);
        m_jsOut = new JSONObject();
        m_jsJmxData = new JSONObject();
        m_jsJdbcData = new JSONObject();
        m_jsDerivedData = new JSONObject();
        
        m_jsOut.put("brannockVersion", "v1");
        m_jsOut.put("startupTime", System.currentTimeMillis());
        m_jsOut.put("brannockErrors", new JSONArray());

        doJmxAttributes();
        doJdbcAttributes();
        doDerivedValues();
        
        m_jsOut.put("jmxData", m_jsJmxData);
        m_jsOut.put("jdbcData", m_jsJdbcData);
        m_jsOut.put("derivedData", m_jsDerivedData);
        m_jsOut.put("finishTime", System.currentTimeMillis());
        writeData(m_jsOut.toString(2));
    }
    
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        
    }
    
    private void doJmxAttributes() {
        addJmxAttribute("java.lang:type=Runtime", "Name", "StartTime", "Uptime", "VmName", "VmVendor", "VmVersion");
        addJmxAttribute("java.lang:type=OperatingSystem", "AvailableProcessors", "Name", "OpenFileDescriptorCount", "TotalPhysicalMemorySize", "Version");
        addJmxAttribute("org.opennms.netmgt.eventd:name=eventlogs.process", "50thPercentile", "75thPercentile", "95thPercentile", "98thPercentile", "99thPercentile", "999thPercentile", "Count", "DurationUnit", "FifteenMinuteRate", "FiveMinuteRate", "Max", "Mean", "MeanRate", "MeanRate", "Min", "OneMinuteRate", "RateUnit", "StdDev");
        addJmxAttribute("OpenNMS:Name=Queued", "CreatesCompleted", "DequeuedItems", "DequeuedOperations", "ElapsedTime", "EnqueuedOperations", "Errors", "PromotionCount", "SignificantOpsCompleted", "SignificantOpsDequeued", "SignificantOpsEnqueued", "StartTime", "TotalOperationsPending", "UpdatesCompleted");
        addJmxAttribute("org.opennms.newts:name=repository.samples-inserted", "Count", "FifteenMinuteRate", "FiveMinuteRate", "MeanRate", "OneMinuteRate", "RateUnit");
        addJmxAttribute("OpenNMS:Name=Pollerd", "ActiveThreads", "CorePoolThreads", "MaxPoolThreads", "NumPolls", "NumPoolThreads", "PeakPoolThreads", "TaskCompletionRatio", "TaskQueuePendingCount", "TaskQueueRemainingCapacity", "TasksCompleted", "TasksTotal");
        addJmxAttribute("org.opennms.netmgt.flows:name=flowsPersisted", "Count", "FifteenMinuteRate", "FiveMinuteRate", "MeanRate", "OneMinuteRate", "RateUnit");        
    }
    
    private void doJdbcAttributes() throws SQLException {
        addJdbcAttribute("SELECT COUNT(*) AS monitoringLocationCount FROM monitoringlocations", "monitoringLocationCount");
        addJdbcAttribute("SELECT COUNT(*) AS minionCount FROM monitoringsystems WHERE type = 'Minion'", "minionCount");
        addJdbcAttribute("SELECT COUNT(*) AS nodeCount FROM node", "nodeCount");
        addJdbcAttribute("SELECT COUNT(distinct foreignsource) AS fsCount FROM node", "fsCount");
        addJdbcAttribute("SELECT COUNT(*) AS totalIPCount FROM ipinterface", "totalIPCount");
        addJdbcAttribute("SELECT COUNT(*) AS managedIPCount FROM ipinterface WHERE ismanaged = 'M'", "managedIPCount");
        addJdbcAttribute("SELECT COUNT(*) AS totalSnmpCount FROM snmpinterface", "totalSnmpCount");
        addJdbcAttribute("SELECT COUNT(*) AS collectSnmpCount FROM snmpinterface WHERE snmpcollect = 'C'", "collectSnmpCount");
        addJdbcAttribute("SELECT COUNT(*) AS serviceCount FROM ifservices", "serviceCount");
    }
    
    private void doDerivedValues() {
        try {
            m_vmUptime = m_jsJmxData.getJSONObject("java.lang:type=Runtime").getLong("Uptime");
            m_pollerTasksCompleted = m_jsJmxData.getJSONObject("OpenNMS:Name=Pollerd").getLong("TasksCompleted");
            m_enqueuedOps = m_jsJmxData.getJSONObject("OpenNMS:Name=Queued").getLong("EnqueuedOperations");
            m_newtsSamplesInsertedMeanRate = m_jsJmxData.getJSONObject("org.opennms.newts:name=repository.samples-inserted").getDouble("MeanRate");
            m_eventLogsProcessMeanRate = m_jsJmxData.getJSONObject("org.opennms.netmgt.eventd:name=eventlogs.process").getDouble("MeanRate");
            m_flowsPersistedMeanRate = m_jsJmxData.getJSONObject("org.opennms.netmgt.flows:name=flowsPersisted").getDouble("MeanRate");
        } catch (JSONException je) {
            accumulateThrowable(je);
        }
        
        addDerivedValue("metricsPersistedPerSec", deriveMetricsPerSec());
        addDerivedValue("pollsCompletedPerSec", derivePollsPerSec());
    }
    
    private Double derivePollsPerSec() {
        Double pps;
        if (m_vmUptime != null &&
                m_vmUptime != 0 &&
                m_pollerTasksCompleted != null) {
            pps = m_pollerTasksCompleted.doubleValue() * 1000.0 / m_vmUptime.doubleValue();
        } else {
            pps = Double.MIN_VALUE;
        }
        return pps;
    }
    
    private Double deriveMetricsPerSec() {
        Double mps;
        if (m_newtsSamplesInsertedMeanRate != null &&
                m_newtsSamplesInsertedMeanRate != Double.NaN && 
                m_newtsSamplesInsertedMeanRate > 0) {
            mps = m_newtsSamplesInsertedMeanRate;
        } else if (m_vmUptime != null &&
                    m_enqueuedOps != null &&
                    m_enqueuedOps > 0) {
            mps = m_enqueuedOps.doubleValue() * 1000.0 / m_vmUptime.doubleValue();
        } else if (m_enqueuedOps == 0) {
            mps = new Double(0);
        } else {
            mps = Double.MIN_VALUE;
        }
        return mps;
    }
    
    private void addJmxAttribute(String objectName, String... attributeNames) {
        ObjectName objNameActual;
        try {
            objNameActual = new ObjectName(objectName);
        } catch (MalformedObjectNameException e) {
            accumulateThrowable(e);
            return;
        }
        JSONObject objJSON = new JSONObject();
        for (String anAttributeName : attributeNames) {
            Object attrObj;
            try {
                attrObj = m_mbeanServer.getAttribute(objNameActual, anAttributeName);
            } catch (InstanceNotFoundException | AttributeNotFoundException
                    | ReflectionException | MBeanException e) {
                accumulateThrowable(e);
                continue;
            }
            if (attrObj instanceof Double && (Double.isInfinite((Double)attrObj) || Double.isNaN((Double)attrObj))) {
                objJSON.put(anAttributeName, attrObj.toString());
            } else {
                objJSON.put(anAttributeName, attrObj);
            }
        }
        m_jsJmxData.put(objectName, objJSON);
    }
    
    private void addJdbcAttribute(String query, String... columns) throws SQLException {
        Connection conn = m_jdbcDS.getConnection();
        PreparedStatement st = conn.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            for (String aColumn: columns) {
                long aValue = rs.getInt(aColumn);
                m_jsJdbcData.put(aColumn, aValue);
            }
        }
    }
    
    private void addDerivedValue(String name, Object value) {
        m_jsDerivedData.put(name, value);
    }
    
    private DataSource getJdbcDataSource(BundleContext context) {
        Class<DataSource> type = DataSource.class;
        ServiceReference<DataSource> ref = context.getServiceReference(type);
        if(ref != null) {
            return context.getService(ref);
        }
        return null;
    }
    
    private void accumulateThrowable(Throwable t) {
        m_jsOut.append("brannockErrors", t);
    }
    
    private void writeData(String data) throws IOException {
        String onmsHome = System.getenv("OPENNMS_HOME");
        if (onmsHome == null || "".equals(onmsHome) ) {
            onmsHome = "/opt/opennms"; // TODO: debian exists
        }
        String fileName = onmsHome + "/logs/brannock_stats.json";
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(file, true);
        Writer output = new BufferedWriter(writer);
        output.append(data);
        output.close();
    }
}
