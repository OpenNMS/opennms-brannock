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

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.json.JSONObject;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Brannock implements BundleActivator {
    private MBeanServer m_mbeanServer;
    private JSONObject m_jsOut;
    private JSONObject m_jsJmxData;
    
    @Override
    public void start(BundleContext bundleContext) throws Exception {
        m_mbeanServer = ManagementFactory.getPlatformMBeanServer();
        m_jsOut = new JSONObject();
        
        m_jsJmxData = new JSONObject();
        
        m_jsOut.put("brannockVersion", "v1");

        addAttribute("java.lang:type=Runtime", "Name", "StartTime", "Uptime", "VmName", "VmVendor", "VmVersion");
        addAttribute("java.lang:type=OperatingSystem", "AvailableProcessors", "Name", "OpenFileDescriptorCount", "TotalPhysicalMemorySize", "Version");
        addAttribute("org.opennms.netmgt.eventd:name=eventlogs.process", "50thPercentile", "75thPercentile", "95thPercentile", "98thPercentile", "99thPercentile", "999thPercentile", "Count", "DurationUnit", "FifteenMinuteRate", "FiveMinuteRate", "Max", "Mean", "MeanRate", "MeanRate", "Min", "OneMinuteRate", "RateUnit", "StdDev");
        addAttribute("OpenNMS:Name=Queued", "CreatesCompleted", "DequeuedItems", "DequeuedOperations", "ElapsedTime", "EnqueuedOperations", "Errors", "PromotionCount", "SignificantOpsCompleted", "SignificantOpsDequeued", "SignificantOpsEnqueued", "StartTime", "TotalOperationsPending", "UpdatesCompleted");
        addAttribute("org.opennms.newts:name=repository.samples-inserted", "Count", "FifteenMinuteRate", "FiveMinuteRate", "MeanRate", "OneMinuteRate", "RateUnit");
        addAttribute("OpenNMS:Name=Pollerd", "ActiveThreads", "CorePoolThreads", "MaxPoolThreads", "NumPolls", "NumPoolThreads", "PeakPoolThreads", "TaskCompletionRatio", "TaskQueuePendingCount", "TaskQueueRemainingCapacity", "TasksCompleted", "TasksTotal");
        addAttribute("org.opennms.netmgt.flows:name=flowsPersisted", "Count", "FifteenMinuteRate", "FiveMinuteRate", "MeanRate", "OneMinuteRate", "RateUnit");
        
        m_jsOut.put("jmxData", m_jsJmxData);
        writeData(m_jsOut.toString(2));
    }
    
    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        
    }
    
    private void addAttribute(String objectName, String... attributeNames) {
        try {
            ObjectName objNameActual = new ObjectName(objectName);
            JSONObject objJSON = new JSONObject();
            for (String anAttributeName : attributeNames) {
                Object attrObj = m_mbeanServer.getAttribute(objNameActual, anAttributeName);
                objJSON.put(anAttributeName, attrObj.toString());
            }
            m_jsJmxData.put(objectName, objJSON);
        } catch (Throwable t) {
            // gulp
        }
    }
    
    private void writeData(String data) throws IOException {
        String fileName = System.getenv("OPENNMS_HOME") + "/logs/brannock_stats.json";
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
