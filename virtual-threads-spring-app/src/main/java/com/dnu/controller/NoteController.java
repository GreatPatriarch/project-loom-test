package com.dnu.controller;

import com.dnu.controller.payload.NotePayload;
import com.dnu.entity.Note;
import com.dnu.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notes")
public class NoteController {
    private final NoteService noteService;

    @GetMapping("/{userId:\\d+}")
    public List<Note> getNotes(@PathVariable Long userId) {
        return noteService.getNotesByUser(userId);
    }

    @PostMapping("/note-create/{userId:\\d+}")
    public ResponseEntity<Note> addNote(@PathVariable(name = "userId") Long userId, @RequestBody NotePayload payload) {
        return new ResponseEntity<>(noteService.addNote(userId, payload.title(), payload.content()), HttpStatus.CREATED);
    }

    @PatchMapping("/user/{userId:\\d+}/note-update/{noteId:\\d+}")
    public Note updateNote(@PathVariable(name = "userId") Long userId, @PathVariable(name = "noteId") Long noteId, @RequestBody NotePayload payload) {
        return noteService.updateNoteForUser(userId, noteId, payload.title(), payload.content());
    }

    @DeleteMapping("/user/{userId:\\d+}/note-delete/{noteId:\\d+}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long userId, @PathVariable Long noteId) {
        noteService.deleteNoteFromUser(userId, noteId);
        return ResponseEntity.noContent().build();
    }
}
