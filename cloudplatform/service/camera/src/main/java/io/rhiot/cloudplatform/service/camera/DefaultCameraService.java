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
package io.rhiot.cloudplatform.service.camera;

import org.apache.camel.ProducerTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultCameraService implements CameraService {

    private final ProducerTemplate producerTemplate;

    public DefaultCameraService(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public List<PlateMatch> recognizePlate(String country, byte[] imageData) {
        return ((List<io.rhiot.cloudplatform.camel.openalpr.PlateMatch>) producerTemplate.requestBody("openalpr:recognize?country=" + country, imageData, List.class)).stream().
                map(match -> new PlateMatch(match.getPlateNumber(), match.getConfidence())).collect(Collectors.toList());
    }

    @Override
    public void process(String deviceId, byte[] imageData) {

    }

}