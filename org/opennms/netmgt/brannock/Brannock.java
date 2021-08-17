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

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.json.JSONObject;

public class Brannock {
    private List<Attribute> m_targetAttributes;
    private MBeanServer m_mbeanServer;
    private JSONObject m_jsOut;
    private JSONObject m_jsJmxData;
    
    public static void main(String[] args) {
        Brannock brannock = new Brannock();
        brannock.start();
    }
    
    public void start() {
        m_mbeanServer = ManagementFactory.getPlatformMBeanServer();
        m_targetAttributes = new ArrayList<>();
        m_jsOut = new JSONObject();
        
        m_jsJmxData = new JSONObject();
        
        m_jsOut.put("brannockVersion", "v1");

        addAttribute("java.lang:type=Runtime", "Name", "StartTime", "Uptime", "VmName", "VmVendor", "VmVersion");
        addAttribute("java.lang:type=OperatingSystem", "AvailableProcessors", "Name", "OpenFileDescriptorCount", "TotalPhysicalMemorySize", "Version");
//        addAttribute("org.opennms.netmgt.eventd:name=eventlogs.process", "Mean");        
        
        m_jsOut.put("jmxData", m_jsJmxData);
        System.out.println(m_jsOut.toString());
    }
    
    private void addAttribute(String objectName, String... attributeNames) {
        try {
            ObjectName objNameActual = new ObjectName(objectName);
            JSONObject objJSON = new JSONObject();
            for (String anAttributeName : attributeNames) {
                Object attrObj = m_mbeanServer.getAttribute(objNameActual, anAttributeName);
                JSONObject attrJSON = new JSONObject().put(anAttributeName, attrObj.toString());
                objJSON.put(objectName, attrJSON);
            }
            m_jsJmxData.put(objectName, objJSON);
        } catch (Throwable t) {
            System.err.println("Oops: " + t.getMessage());
        }
    }
    
}
