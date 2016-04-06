/*
*  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.apimgt.gateway.throttling;

import org.apache.axis2.transport.base.threads.NativeWorkerPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.wso2.carbon.apimgt.gateway.throttling.util.jms.*;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.commons.exception.TransportException;
import org.wso2.carbon.databridge.commons.utils.DataBridgeCommonsUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class will hold throttle data per given node. All throttle handler objects should refer values from this.
 * When throttle data holder initialize it should read complete throttle decision table from global policy engine
 * via web service calls. In addition to that it should subscribe to topic and listen throttle updates.
 */

public class ThrottleDataHolder {

        private static final Log log = LogFactory.getLog(ThrottleDataHolder.class);
        private String streamID;

        public Map<String, String> getThrottleDataMap() {
            return throttleDataMap;
        }

        public void setThrottleDataMap(Map<String, String> throttleDataMap) {
            this.throttleDataMap = throttleDataMap;
        }


        Map<String, String> throttleDataMap = new ConcurrentHashMap();

        /**
         * This method will check given key in throttle data Map. Throttle data map need to be update from topic
         * subscriber with all latest updates from global policy engine. This method will perfoem only local map
         * lookup and return results.
         *
         * @param key String unique key of throttle event.
         * @return Return true if event throttled(means key is available in throttle data map).
         * false if key is not there in throttle map(that means its not throttled).
         */
        //simplyfy if
        public boolean isThrottled(String key) {
            if ( this.throttleDataMap.get(key) !=null) {
                return true;
            } else {
                return false;
            }
        }



        /**
         * This method will used to push un-throttled data events to global policy engine.
         *
         * @param throttleRequest is objects map which contains certain information retrieved from incoming message
         *                        need to add as much as data to this map and send events.
         */
        public void sendToGlobalThrottler(Object[] throttleRequest, DataPublisher dataPublisher) {
            org.wso2.carbon.databridge.commons.Event event = new org.wso2.carbon.databridge.commons.Event(streamID,
                    System.currentTimeMillis(), null, null, throttleRequest);
            dataPublisher.tryPublish(event);
        }


    }
