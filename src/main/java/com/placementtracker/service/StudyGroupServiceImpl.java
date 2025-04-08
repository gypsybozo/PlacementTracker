package com.placementtracker.service;

import com.placementtracker.dto.StudyGroupDto;
import com.placementtracker.model.StudyGroup;
import com.placementtracker.model.User;
import com.placementtracker.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudyGroupServiceImpl implements StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;

    @Override
    @Transactional
    public StudyGroup createGroup(User creator, StudyGroupDto groupDto) {
        StudyGroup group = new StudyGroup();
        group.setName(groupDto.getName());
        group.setDescription(groupDto.getDescription());
        group.setCreator(creator);
        
        // Add creator as a member
        group.addMember(creator);
        
        // Generate a unique invitation code
        String invitationCode = UUID.randomUUID().toString().substring(0, 8);
        group.setInvitationCode(invitationCode);
        
        return studyGroupRepository.save(group);
    }

    @Override
    public List<StudyGroup> getUserGroups(User user) {
        return studyGroupRepository.findByUserMembership(user);
    }

    @Override
    public Optional<StudyGroup> getGroupById(Long id) {
        return studyGroupRepository.findById(id);
    }

    @Override
    public Optional<StudyGroup> getGroupByInvitationCode(String code) {
        return studyGroupRepository.findByInvitationCode(code);
    }

    @Override
    @Transactional
    public boolean addMemberToGroup(StudyGroup group, User user) {
        if (group.isMember(user)) {
            return false; // User is already a member
        }
        
        group.addMember(user);
        studyGroupRepository.save(group);
        return true;
    }

    @Override
    @Transactional
    public boolean removeMemberFromGroup(StudyGroup group, User user) {
        if (group.getCreator().equals(user)) {
            return false; // Cannot remove the creator
        }
        
        if (!group.isMember(user)) {
            return false; // User is not a member
        }
        
        group.removeMember(user);
        studyGroupRepository.save(group);
        return true;
    }

    @Override
    @Transactional
    public StudyGroup updateGroup(StudyGroup group, StudyGroupDto groupDto) {
        group.setName(groupDto.getName());
        group.setDescription(groupDto.getDescription());
        return studyGroupRepository.save(group);
    }

    @Override
    @Transactional
    public boolean deleteGroup(StudyGroup group) {
        studyGroupRepository.delete(group);
        return true;
    }

    @Override
    @Transactional
    public String generateInvitationCode(StudyGroup group) {
        String invitationCode = UUID.randomUUID().toString().substring(0, 8);
        group.setInvitationCode(invitationCode);
        studyGroupRepository.save(group);
        return invitationCode;
    }
}