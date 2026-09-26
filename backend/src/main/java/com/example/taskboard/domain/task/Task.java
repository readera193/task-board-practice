package com.example.taskboard.domain.task;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.example.taskboard.domain.user.User;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    protected Task() {}

    public Task(Long id, String title, boolean completed, LocalDateTime createdAt,
            String description, User user) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
        this.description = description;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public void toggle() {
        completed = !completed;
    }
}
