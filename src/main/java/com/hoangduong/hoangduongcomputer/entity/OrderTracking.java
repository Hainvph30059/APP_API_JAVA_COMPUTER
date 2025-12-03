package com.hoangduong.hoangduongcomputer.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderTracking {

    String status;
    String description;
    String location;

    @Builder.Default
    Instant timestamp = Instant.now();

    String updatedBy;
}
