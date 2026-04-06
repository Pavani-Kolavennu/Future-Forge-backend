package com.futureforge.announcement;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/admin/announcements", "/admin/announcements"})
public class AdminAnnouncementController {
	private final AnnouncementService announcementService;

	public AdminAnnouncementController(AnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	@GetMapping
	public List<Announcement> getAllAnnouncements() {
		return announcementService.findAll();
	}

	@GetMapping("/active")
	public List<Announcement> getActiveAnnouncements() {
		return announcementService.findActive();
	}

	@GetMapping("/{announcementId}")
	public Announcement getAnnouncement(@PathVariable Long announcementId) {
		return announcementService.getById(announcementId);
	}

	@PostMapping
	public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody CreateAnnouncementDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(announcementService.create(dto));
	}

	@PutMapping("/{announcementId}")
	public Announcement updateAnnouncement(@PathVariable Long announcementId, @Valid @RequestBody CreateAnnouncementDto dto) {
		return announcementService.update(announcementId, dto);
	}

	@DeleteMapping("/{announcementId}")
	public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long announcementId) {
		announcementService.delete(announcementId);
		return ResponseEntity.noContent().build();
	}
}
