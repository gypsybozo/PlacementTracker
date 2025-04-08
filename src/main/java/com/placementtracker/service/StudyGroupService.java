package com.placementtracker.service;

import com.placementtracker.dto.StudyGroupDto;
import com.placementtracker.model.StudyGroup;
import com.placementtracker.model.User;

import java.util.List;
import java.util.Optional;

public interface StudyGroupService {
    StudyGroup createGroup(User creator, StudyGroupDto groupDto);
    List<StudyGroup> getUserGroups(User user);
    Optional<StudyGroup> getGroupById(Long id);
    Optional<StudyGroup> getGroupByInvitationCode(String code);
    boolean addMemberToGroup(StudyGroup group, User user);
    boolean removeMemberFromGroup(StudyGroup group, User user);
    StudyGroup updateGroup(StudyGroup group, StudyGroupDto groupDto);
    boolean deleteGroup(StudyGroup group);
    String generateInvitationCode(StudyGroup group);
}