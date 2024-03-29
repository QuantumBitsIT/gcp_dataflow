/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.teleport.v2.values;

import com.google.cloud.teleport.v2.coders.FailsafeElementCoder;
import java.util.Objects;
import org.apache.avro.reflect.Nullable;
import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.vendor.guava.v32_1_2_jre.com.google.common.base.MoreObjects;

/**
 * The {@link FailsafeElement} class holds the current value and original value of a record within a
 * pipeline. This class allows pipelines to not lose valuable information about an incoming record
 * throughout the processing of that record. The use of this class allows for more robust
 * dead-letter strategies as the original record information is not lost throughout the pipeline and
 * can be output to a dead-letter in the event of a failure during one of the pipelines transforms.
 */
@DefaultCoder(FailsafeElementCoder.class)
public class FailsafeElement<OriginalT, CurrentT> {

  private final OriginalT originalPayload;
  private final CurrentT payload;
  @Nullable private String errorMessage;
  @Nullable private String stacktrace;

  private FailsafeElement(OriginalT originalPayload, CurrentT payload) {
    this.originalPayload = originalPayload;
    this.payload = payload;
  }

  public static <OriginalT, CurrentT> FailsafeElement<OriginalT, CurrentT> of(
      OriginalT originalPayload, CurrentT currentPayload) {
    return new FailsafeElement<>(originalPayload, currentPayload);
  }

  public static <OriginalT, CurrentT> FailsafeElement<OriginalT, CurrentT> of(
      FailsafeElement<OriginalT, CurrentT> other) {
    return new FailsafeElement<>(other.originalPayload, other.payload)
        .setErrorMessage(other.getErrorMessage())
        .setStacktrace(other.getStacktrace());
  }

  public OriginalT getOriginalPayload() {
    return originalPayload;
  }

  public CurrentT getPayload() {
    return payload;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public FailsafeElement<OriginalT, CurrentT> setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  public String getStacktrace() {
    return stacktrace;
  }

  public FailsafeElement<OriginalT, CurrentT> setStacktrace(String stacktrace) {
    this.stacktrace = stacktrace;
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    final FailsafeElement other = (FailsafeElement) obj;
    return Objects.deepEquals(this.originalPayload, other.getOriginalPayload())
        && Objects.deepEquals(this.payload, other.getPayload())
        && Objects.deepEquals(this.errorMessage, other.getErrorMessage())
        && Objects.deepEquals(this.stacktrace, other.getStacktrace());
  }

  @Override
  public int hashCode() {
    return Objects.hash(originalPayload, payload, errorMessage, stacktrace);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("originalPayload", originalPayload)
        .add("payload", payload)
        .add("errorMessage", errorMessage)
        .add("stacktrace", stacktrace)
        .toString();
  }
}
