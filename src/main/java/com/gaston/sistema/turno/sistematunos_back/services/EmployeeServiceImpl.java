package com.gaston.sistema.turno.sistematunos_back.services;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gaston.sistema.turno.sistematunos_back.dto.ChangePasswordRequest;
import com.gaston.sistema.turno.sistematunos_back.dto.EmployeeDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.UpdateEmployeeProfileRequest;
import com.gaston.sistema.turno.sistematunos_back.entities.Employee;
import com.gaston.sistema.turno.sistematunos_back.entities.Schedule;
import com.gaston.sistema.turno.sistematunos_back.entities.ShopImage;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.EmployeeRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopImageRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.EmailAlreadyExistsException;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private final EmployeeRepository employeeRepository;
    private final ShopService shopService;
    private final ShopImageRepository shopImageRepository;
    private final CloudinaryService cloudinaryService;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, ShopService shopService,
            ShopImageRepository shopImageRepository, CloudinaryService cloudinaryService, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.shopService = shopService;
        this.shopImageRepository = shopImageRepository;
        this.cloudinaryService = cloudinaryService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public EmployeeDTO createEmployee(Employee employee, Long ownerId, MultipartFile file) {
           if(employeeRepository.findByEmail(employee.getEmail()).isPresent()){
               throw new EmailAlreadyExistsException("Email ya registrado");
           }

           Shop shopDb = shopService.getByOwner(ownerId);
           // Exclude the owner ghost profile from the 5-employee real limit
           if (employeeRepository.countByShopIdAndRoleNot(shopDb.getId(), "OWNER_PROVIDER") >= 5) {
              throw new IllegalArgumentException("No puedes tener mas de 5 empleados");
           }

           try{
                if (file != null && !file.isEmpty()) {
                    String[] uploadResult = cloudinaryService.uploadImage(file, "employee-images");
                    
                    ShopImage employeeImage = new ShopImage();
                    employeeImage.setFileName(file.getOriginalFilename());
                    employeeImage.setFileType(file.getContentType());
                    employeeImage.setImageUrl(uploadResult[0]);
                    employeeImage.setCloudinaryPublicId(uploadResult[1]);
                    employee.setEmployeeImage(employeeImage);
                }
           }catch(Exception e){
              throw new RuntimeException("Error al guardar la imagen del empleado: " + e);
           }

           shopDb.getEmployees().add(employee);

           employee.setPassword(passwordEncoder.encode(employee.getPassword()));
           employee.setShop(shopDb);
           employee.setRole("EMPLEADO");

           Employee newEmployee = employeeRepository.save(employee);

           // Inherit active shop schedules: copy each active schedule from the shop to the new employee
           List<Schedule> shopSchedules = shopDb.getSchedules();
           for (Schedule shopSchedule : shopSchedules) {
               if (shopSchedule.isActive()) {
                   Schedule employeeSchedule = new Schedule();
                   employeeSchedule.setDayOfWeek(shopSchedule.getDayOfWeek());
                   employeeSchedule.setOpeningTime(shopSchedule.getOpeningTime());
                   employeeSchedule.setClosingTime(shopSchedule.getClosingTime());
                   employeeSchedule.setActive(true);
                   employeeSchedule.setEmployee(newEmployee);
                   newEmployee.getSchedules().add(employeeSchedule);
               }
           }
           employeeRepository.save(newEmployee);
           log.info("Employee id={} created with {} inherited schedules", newEmployee.getId(), newEmployee.getSchedules().size());

           return toEmployeeDTO(newEmployee);
    }

    @Override
    public EmployeeDTO editEmployee(Employee employee, MultipartFile file, Long employeeId, Long ownerId) {
            Employee employeeDb = employeeRepository.findById(employeeId).orElseThrow(() ->
                                     new IllegalArgumentException("No se encontro el empleado con ese id " + employeeId));

            if (!employeeDb.getShop().getOwner().getId().equals(ownerId)) {
              throw new AccessDeniedException("No tienes permisos para editar este empleado");
            }

            employeeDb.setLastName(employee.getLastName());
            employeeDb.setSpecialty(employee.getSpecialty());
            employeeDb.setName(employee.getName());
            employeeDb.setEmail(employee.getEmail());

            try{
                if(file != null && !file.isEmpty()){
                    ShopImage img = employeeDb.getEmployeeImage();
                    if (img != null && img.getCloudinaryPublicId() != null) {
                        cloudinaryService.deleteImage(img.getCloudinaryPublicId());
                        shopImageRepository.delete(img);
                    }
                    
                    String[] uploadResult = cloudinaryService.uploadImage(file, "employee-images");

                    ShopImage employeeImage = new ShopImage();
                    employeeImage.setFileName(file.getOriginalFilename());
                    employeeImage.setFileType(file.getContentType());
                    employeeImage.setImageUrl(uploadResult[0]);
                    employeeImage.setCloudinaryPublicId(uploadResult[1]);
                    employeeDb.setEmployeeImage(employeeImage);
               }
            } catch (Exception e) {
                throw new RuntimeException("Error al actualizar la imagen del empleado: " + e);
            }

            Employee editedEmployee = employeeRepository.save(employeeDb);
            return toEmployeeDTO(editedEmployee);
    }

    @Override
    public void deleteEmployee(Long employeeId, Long ownerId) {
        Employee employeeDb = employeeRepository.findById(employeeId).orElseThrow(()->
                         new IllegalArgumentException("no se encontro el empleado con ese id" + employeeId));

         if (!employeeDb.getShop().getOwner().getId().equals(ownerId)) {
              throw new AccessDeniedException("No tienes permisos para eliminar este empleado");
        }
        employeeRepository.deleteById(employeeId);
     }

     @Override
     public List<EmployeeDTO> getEmployees(Long ownerId) {
        Shop shopDb = shopService.getByOwner(ownerId);
         return employeeRepository.findByShopId(shopDb.getId()).stream()
                                     .map(emp -> toEmployeeDTO(emp))
                                     .toList();
      }

    @Override
    public EmployeeDTO getEmployee(Long employeeId, Long ownerId) {
        Employee employeeDb = employeeRepository.findById(employeeId).orElseThrow(()->
                            new IllegalArgumentException("no se encontro el empleado con ese id" + employeeId));

        if (!employeeDb.getShop().getOwner().getId().equals(ownerId)) {
              throw new AccessDeniedException("No tienes permisos para ver este empleado");
        }
          return toEmployeeDTO(employeeDb);
    }

    @Override
    public Employee getEmployeeEntity(Long employeeId) {
        return employeeRepository.findById(employeeId).orElseThrow(()->
                                new IllegalArgumentException("error al encontrar al empleado"));
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public EmployeeDTO updateOwnProfile(Long employeeId, UpdateEmployeeProfileRequest request) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new IllegalArgumentException("Employee not found with id: " + employeeId));

        // Check email uniqueness if it changed
        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())) {
            employeeRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(employeeId)) {
                    throw new com.gaston.sistema.turno.sistematunos_back.validation.EmailAlreadyExistsException(
                            "Email is already in use by another account");
                }
            });
        }

        employee.setName(request.getName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setSpecialty(request.getSpecialty());

        Employee updated = employeeRepository.save(employee);
        log.info("Employee id={} updated their own profile", employeeId);
        return toEmployeeDTO(updated);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void changePassword(Long employeeId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new IllegalArgumentException("Employee not found with id: " + employeeId));
        if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);
        log.info("Employee id={} changed their password", employeeId);
    }

    public EmployeeDTO toEmployeeDTO(Employee employee){
            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(employee.getId());
            dto.setLastName(employee.getLastName());
            dto.setName(employee.getName());
            dto.setEmail(employee.getEmail());
            dto.setRole(employee.getRole());
            dto.setSpecialty(employee.getSpecialty());

            if(employee.getEmployeeImage() != null){
                dto.setImageUrl(employee.getEmployeeImage().getImageUrl());
            }
            return dto;
    }
}
