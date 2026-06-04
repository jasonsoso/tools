package com.tools.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tools.entity.MarkdownDoc;
import com.tools.mapper.MarkdownDocMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MarkdownDocRepository {

    private final MarkdownDocMapper markdownDocMapper;

    public List<MarkdownDoc> findByUserIdOrderByUpdatedAtDesc(Long userId) {
        LambdaQueryWrapper<MarkdownDoc> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarkdownDoc::getUserId, userId)
               .orderByDesc(MarkdownDoc::getUpdatedAt);
        return markdownDocMapper.selectList(wrapper);
    }

    public MarkdownDoc findById(Long id) {
        return markdownDocMapper.selectById(id);
    }

    public void save(MarkdownDoc doc) {
        if (doc.getId() == null) {
            markdownDocMapper.insert(doc);
        } else {
            markdownDocMapper.updateById(doc);
        }
    }

    public void update(MarkdownDoc doc) {
        markdownDocMapper.updateById(doc);
    }

    public void deleteById(Long id) {
        markdownDocMapper.deleteById(id);
    }
}
