/**
 * Licensed to the Rhiot under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.rhiot.datastream.source.leshan

import io.rhiot.datastream.engine.test.DataStreamTest
import io.rhiot.datastream.schema.device.Device
import org.junit.After
import org.junit.Test

import static com.github.camellabs.iot.cloudlet.device.client.LeshanClientTemplate.createVirtualLeshanClientTemplate
import static com.google.common.truth.Truth.assertThat
import static io.rhiot.datastream.schema.device.DeviceConstants.getDevice
import static io.rhiot.utils.Uuids.uuid

class LeshanDataStreamSourceTest extends DataStreamTest {

    def deviceId = uuid()

    def leshanClient = createVirtualLeshanClientTemplate(deviceId)

    @Override
    protected void afterDataStreamStarted() {
        leshanClient.connect()
    }

    @After
    void after() {
        leshanClient.disconnect()
    }

    @Test
    void shouldRegisterDevice() {
        def device = fromBus(getDevice(deviceId), Device.class)
        assertThat(device.registrationId).isNotEmpty()
    }

    @Test
    void shouldRegisterDeviceWithId() {
        def device = fromBus(getDevice(deviceId), Device.class)
        assertThat(device.deviceId).isEqualTo(deviceId)
    }

}