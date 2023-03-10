package ru.practicum.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.item.Comment;
import ru.practicum.item.CommentDto;
import ru.practicum.item.Item;
import ru.practicum.user.User;

import java.util.List;
import java.util.stream.Collectors;


@NoArgsConstructor
public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static List<CommentDto> toListCommentsDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setText(commentDto.getText());
        return comment;
    }
}
