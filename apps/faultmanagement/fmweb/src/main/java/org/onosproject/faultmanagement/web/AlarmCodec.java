/*
 * Copyright 2015 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.faultmanagement.web;

import com.fasterxml.jackson.databind.JsonNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.google.common.base.Preconditions.checkNotNull;
import org.onosproject.net.DeviceId;
import org.onosproject.incubator.net.faultmanagement.alarm.Alarm;
import org.onosproject.incubator.net.faultmanagement.alarm.AlarmEntityId;
import org.onosproject.incubator.net.faultmanagement.alarm.AlarmId;
import org.onosproject.incubator.net.faultmanagement.alarm.DefaultAlarm;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Implementation of encoder for Alarm codec.
 */
public final class AlarmCodec extends JsonCodec<Alarm> {

    private final Logger log = getLogger(getClass());

    @Override
    public ObjectNode encode(final Alarm alarm, final CodecContext context) {
        checkNotNull(alarm, "Alarm cannot be null");

        return context.mapper().createObjectNode()
                .put("id", alarm.id().fingerprint())
                .put("deviceId", alarm.deviceId().toString())
                .put("description", alarm.description())
                .put("source",
                        alarm.source() == null ? null
                                : alarm.source().toString())
                .put("timeRaised", alarm.timeRaised())
                .put("timeUpdated", alarm.timeUpdated())
                .put("timeCleared", alarm.timeCleared())
                .put("severity", alarm.severity().toString())
                .put("serviceAffecting", alarm.serviceAffecting())
                .put("acknowledged", alarm.acknowledged())
                .put("manuallyClearable", alarm.manuallyClearable())
                .put("assignedUser", alarm.assignedUser());

    }

    @Override
    public Alarm decode(final ObjectNode json, final CodecContext context) {
        if (json == null || !json.isObject()) {
            return null;
        }

        log.debug("id={}, full json={} ", json.get("id"), json);
        final Long id = json.get("id").asLong();

        final DeviceId deviceId = DeviceId.deviceId(json.get("deviceId").asText());
        final String description = json.get("description").asText();
        final Long timeRaised = json.get("timeRaised").asLong();
        final Long timeUpdated = json.get("timeUpdated").asLong();

        final JsonNode jsonTimeCleared = json.get("timeCleared");
        final Long timeCleared = jsonTimeCleared == null || jsonTimeCleared.isNull() ? null : jsonTimeCleared.asLong();

        final Alarm.SeverityLevel severity
                = Alarm.SeverityLevel.valueOf(json.get("severity").asText().toUpperCase());

        final Boolean serviceAffecting = json.get("serviceAffecting").asBoolean();
        final Boolean acknowledged = json.get("acknowledged").asBoolean();
        final Boolean manuallyClearable = json.get("manuallyClearable").asBoolean();

        final JsonNode jsonAssignedUser = json.get("assignedUser");
        final String assignedUser
                = jsonAssignedUser == null || jsonAssignedUser.isNull() ? null : jsonAssignedUser.asText();

        return new DefaultAlarm.Builder(
                AlarmId.valueOf(id), deviceId, description, severity, timeRaised).forSource(AlarmEntityId.NONE).
                withTimeUpdated(timeUpdated).
                withTimeCleared(timeCleared).
                withServiceAffecting(serviceAffecting).
                withAcknowledged(acknowledged).
                withManuallyClearable(manuallyClearable).
                withAssignedUser(assignedUser).
                build();

    }
}
