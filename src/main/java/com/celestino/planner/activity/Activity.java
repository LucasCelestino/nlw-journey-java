package com.celestino.planner.activity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.celestino.planner.trip.Trip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "activities")
@Getter
@Setter
@AllArgsConstructor
public class Activity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "occurs_at", nullable = false)
    private LocalDateTime occursAt;

    @ManyToOne
    @Column(name = "trip_id", nullable = false)
    private Trip trip;
}