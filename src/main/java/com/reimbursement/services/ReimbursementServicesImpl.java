package com.reimbursement.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.reimbursement.entities.ImageEntity;
import com.reimbursement.entities.ReimbursementEntity;
import com.reimbursement.models.GenericResponse;
import com.reimbursement.models.Reimbursement;
import com.reimbursement.repositories.ImageRepository;
import com.reimbursement.repositories.ReimbursementRepository;

@Service
public class ReimbursementServicesImpl implements ReimbursementServices {

	@Autowired
	private ReimbursementRepository reimbursementRepo;
	
	@Autowired
	private ImageRepository imageRepo;


	@Override
	public List<Reimbursement> getAllReimbursements() {
		List<Reimbursement> allReimbursement = new ArrayList<Reimbursement>();
		List<ReimbursementEntity> allReimbursementEntity = reimbursementRepo.findAll();
		for (ReimbursementEntity reimbursementEntity : allReimbursementEntity) {
			Reimbursement reimbursement = new Reimbursement(reimbursementEntity.getReimbursementId(),
					reimbursementEntity.getReimbursementAmount(), reimbursementEntity.getReimbursementSubmitTime(),
					reimbursementEntity.getReimbursementResolvedTime(),
					reimbursementEntity.getReimbursementDescription(),
					reimbursementEntity.getReimbursementApplicantID(), reimbursementEntity.getReimbursementStatusID(),
					reimbursementEntity.getImage() != null ? reimbursementEntity.getImage().getImageId() : 0);
			allReimbursement.add(reimbursement);
		}
		return allReimbursement;
	}

	@Override
	public List<Reimbursement> searchReimbursements(Long empId) {
		List<Reimbursement> allReimbursement = new ArrayList<Reimbursement>();
		List<ReimbursementEntity> entities = reimbursementRepo.findByReimbursementApplicantID(empId);
		for (ReimbursementEntity reimEntity : entities) {
			Reimbursement reim = new Reimbursement();
			reim.setReimbursementID(reimEntity.getReimbursementId());
			reim.setReimbursementAmount(reimEntity.getReimbursementAmount());
			reim.setReimbursementDescription(reimEntity.getReimbursementDescription());
			reim.setReimbursementSubmitTime(reimEntity.getReimbursementSubmitTime());
			reim.setReimbursementResolvedTime(reimEntity.getReimbursementResolvedTime());
			reim.setReimbursementApplicantID(reimEntity.getReimbursementApplicantID());

			if(reimEntity.getImage() != null) {
				reim.setImageId(reimEntity.getImage().getImageId());
			}
			allReimbursement.add(reim);
		}
		return allReimbursement;
	}

	@Override
	public boolean updateReimbursement(Reimbursement reimbursement) {
		if (reimbursement.getReimbursementID() != null
				&& reimbursementRepo.existsById(reimbursement.getReimbursementID())) {
			ReimbursementEntity entity = reimbursementRepo.getById(reimbursement.getReimbursementID());
			entity.setReimbursementStatusID(reimbursement.getReimbursementStatusID());
			entity.setReimbursementResolvedTime(new Date());
			reimbursementRepo.save(entity);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public GenericResponse addReimbursement(Reimbursement reimbursement, MultipartFile file) throws IOException {

		ReimbursementEntity entity = new ReimbursementEntity();
		entity.setReimbursementAmount(reimbursement.getReimbursementAmount());
		entity.setReimbursementApplicantID(reimbursement.getReimbursementApplicantID());
		entity.setReimbursementDescription(reimbursement.getReimbursementDescription());
		entity.setReimbursementSubmitTime(new Date());
		entity.setReimbursementStatusID(2L);

		ImageEntity imgEntity = new ImageEntity();
		imgEntity.setImageName(file.getOriginalFilename());
		imgEntity.setReim(entity);
		imgEntity.setData(file.getBytes());
		imgEntity.setContentType(file.getContentType());

		imgEntity = imageRepo.save(imgEntity);
		return new GenericResponse("success");
	}



	public ImageEntity download(Long id) {
		return imageRepo.getById(id);
	}

}

