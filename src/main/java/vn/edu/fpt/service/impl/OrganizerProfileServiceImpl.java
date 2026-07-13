package vn.edu.fpt.service.impl;

import org.springframework.stereotype.Service;
import vn.edu.fpt.common.error.ProfileNotFoundException;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.request.organizer.OrganizerProfileDto;
import vn.edu.fpt.repository.BankRepository;
import vn.edu.fpt.repository.OrganizerProfileRepository;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.service.OrganizerProfileService;

@Service("OrganizerProfileService")
public class OrganizerProfileServiceImpl implements OrganizerProfileService {
    private final OrganizerProfileRepository organizerProfileRepository;
    private final BankRepository bankRepository;
    private final UserRepository userRepository;
    public OrganizerProfileServiceImpl(OrganizerProfileRepository organizerProfileRepository, vn.edu.fpt.repository.BankRepository bankRepository, UserRepository userRepository) {
        this.organizerProfileRepository = organizerProfileRepository;
        this.bankRepository = bankRepository;
        this.userRepository = userRepository;
    }

    public OrganizerProfile handleCreateOrganizerProfile(OrganizerProfile organizerProfile){
        organizerProfileRepository.save(organizerProfile);
        return organizerProfile;
    }

    @Override
    public boolean existsByTaxCode(String taxCode) {
        return this.organizerProfileRepository.existsByTaxCode(taxCode);
    }

    @Override
    public OrganizerProfileDto getOrganizerProfileByUserId(Long userId) {
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserId(userId)
                            .orElseThrow(()->new ProfileNotFoundException("Người dùng này không có organizerProfile"));
        OrganizerProfileDto dto = new OrganizerProfileDto();
        dto.setIdProfile(organizerProfile.getId());
        dto.setTaxCode(organizerProfile.getTaxCode());
        dto.setCompanyName(organizerProfile.getCompanyName());
        dto.setLegalName(organizerProfile.getLegalName());
        dto.setLegalAddress(organizerProfile.getLegalAddress());
        dto.setBankAccountName(organizerProfile.getBankAccountName());
        dto.setBankAccountNumber(organizerProfile.getBankAccountNumber());
        if (organizerProfile.getBank() != null) {
            dto.setBankId(organizerProfile.getBank().getId());
        }
        dto.setBankBranch(organizerProfile.getBankBranch());
        dto.setBusinessType(organizerProfile.getBusinessType());
        return dto;
    }


    @Override
    public void updateProfile(Long userId, OrganizerProfileDto dto) {
        OrganizerProfile organizerProfile = organizerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException("Người dùng này không có organizerProfile"));

        organizerProfile.setTaxCode(dto.getTaxCode());
        organizerProfile.setCompanyName(dto.getCompanyName());
        organizerProfile.setLegalName(dto.getLegalName());
        organizerProfile.setLegalAddress(dto.getLegalAddress());
        organizerProfile.setBankAccountName(dto.getBankAccountName());
        organizerProfile.setBankAccountNumber(dto.getBankAccountNumber());
        organizerProfile.setBankBranch(dto.getBankBranch());
        organizerProfile.setBusinessType(dto.getBusinessType());
            Bank bank = bankRepository.getReferenceById(dto.getBankId());
            organizerProfile.setBank(bank);

        organizerProfileRepository.save(organizerProfile);
    }

    @Override
    public boolean CanCreateEvent(Long userId) {
        User user = userRepository.getReferenceById(userId);
        for(UserRole ur :user.getUserRoles()){
            if (ur.getRole().getRoleName().equals(RoleName.ROLE_ORGANIZER)) {
                if (organizerProfileRepository.getReferenceById(userId).getIsActive()){
                    return true;
                }
            }
        }
        return false;
    }

    public static class FinanceServiceImpl {
    }
}
