package com.dnu.service;

import com.dnu.entity.Note;
import com.dnu.entity.User;
import com.dnu.repository.NoteRepository;
import com.dnu.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    public List<Note> getNotesByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getNotes();
    }

    public Note addNote(Long userId, String title, String content) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("No user by id"));
        Note note = new Note(title, content, user);
        user.addNote(note);
        noteRepository.save(note);
        return note;
    }
    @Transactional
    public Note updateNoteForUser(Long userId, Long noteId, String title, String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User does not have permission to update this note");
        }
        note.setTitle(title);
        note.setContent(content);
        return noteRepository.save(note);
    }

    public void deleteNoteFromUser(Long userId, Long noteId) {
        Note note = noteRepository.findById(noteId).orElseThrow(() -> new RuntimeException("Note not found"));

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User does not have permission to delete this note");
        }

        noteRepository.delete(note);
    }
}
