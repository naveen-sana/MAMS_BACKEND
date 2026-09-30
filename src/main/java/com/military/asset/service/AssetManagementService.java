package com.military.asset.service;

import com.military.asset.dto.*;
import com.military.asset.exception.InsufficientStockException;
import com.military.asset.exception.ResourceNotFoundException;
import com.military.asset.model.*;
import com.military.asset.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AssetManagementService {

    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final PersonnelUnitRepository personnelUnitRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransferRepository transferRepository;
    private final AssignmentRepository assignmentRepository;
    private final ExpenditureRepository expenditureRepository;
    private final AuditLogService auditLogService;

    public AssetManagementService(BaseRepository baseRepository,
                                  EquipmentTypeRepository equipmentTypeRepository,
                                  PersonnelUnitRepository personnelUnitRepository,
                                  PurchaseRepository purchaseRepository,
                                  TransferRepository transferRepository,
                                  AssignmentRepository assignmentRepository,
                                  ExpenditureRepository expenditureRepository,
                                  AuditLogService auditLogService) {
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.personnelUnitRepository = personnelUnitRepository;
        this.purchaseRepository = purchaseRepository;
        this.transferRepository = transferRepository;
        this.assignmentRepository = assignmentRepository;
        this.expenditureRepository = expenditureRepository;
        this.auditLogService = auditLogService;
    }

    // --- Dynamic Inventory Stock Calculation ---
    public int getAvailableStock(Long baseId, Long equipmentTypeId) {
        int purchases = purchaseRepository.sumQuantityByBaseAndEquipmentType(baseId, equipmentTypeId);
        int transfersIn = transferRepository.sumTransfersInByBaseAndEquipmentType(baseId, equipmentTypeId);
        int transfersOut = transferRepository.sumTransfersOutByBaseAndEquipmentType(baseId, equipmentTypeId);
        int assignments = assignmentRepository.sumQuantityByBaseAndEquipmentType(baseId, equipmentTypeId);
        int expenditures = expenditureRepository.sumQuantityByBaseAndEquipmentType(baseId, equipmentTypeId);

        return (purchases + transfersIn) - (transfersOut + assignments + expenditures);
    }

    // --- Dashboard Aggregations ---
    public DashboardSummaryDTO getDashboardSummary(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        List<Purchase> purchasesList = purchaseRepository.filterPurchases(baseId, equipmentTypeId, startDate, endDate);
        List<Transfer> transfersList = transferRepository.filterTransfers(baseId, equipmentTypeId, startDate, endDate);
        List<Assignment> assignmentsList = assignmentRepository.filterAssignments(baseId, equipmentTypeId, startDate, endDate);
        List<Expenditure> expendituresList = expenditureRepository.filterExpenditures(baseId, equipmentTypeId, startDate, endDate);

        int totalPurchases = purchasesList.stream().mapToInt(Purchase::getQuantity).sum();

        int transfersIn = transfersList.stream()
                .filter(t -> baseId == null || t.getToBase().getId().equals(baseId))
                .mapToInt(Transfer::getQuantity)
                .sum();

        int transfersOut = transfersList.stream()
                .filter(t -> baseId != null && t.getFromBase().getId().equals(baseId))
                .mapToInt(Transfer::getQuantity)
                .sum();

        int netMovement = (totalPurchases + transfersIn) - transfersOut;
        int totalAssigned = assignmentsList.stream().mapToInt(Assignment::getQuantity).sum();
        int totalExpended = expendituresList.stream().mapToInt(Expenditure::getQuantity).sum();

        int openingBalance = 1000;
        int closingBalance = (openingBalance + netMovement) - (totalAssigned + totalExpended);

        return DashboardSummaryDTO.builder()
                .openingBalance(openingBalance)
                .purchases(totalPurchases)
                .transfersIn(transfersIn)
                .transfersOut(transfersOut)
                .netMovement(netMovement)
                .closingBalance(closingBalance)
                .assigned(totalAssigned)
                .expended(totalExpended)
                .build();
    }

    // --- Purchases ---
    @Transactional
    public Purchase createPurchase(PurchaseRequestDTO dto) {
        Base base = baseRepository.findById(dto.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + dto.getBaseId()));

        EquipmentType equipmentType = equipmentTypeRepository.findById(dto.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment Type not found with ID: " + dto.getEquipmentTypeId()));

        Purchase purchase = Purchase.builder()
                .base(base)
                .equipmentType(equipmentType)
                .quantity(dto.getQuantity())
                .date(dto.getDate())
                .build();

        Purchase saved = purchaseRepository.save(purchase);
        auditLogService.logAction(null, "RECORD_PURCHASE", "PURCHASE", saved.getId(),
                "Purchased " + saved.getQuantity() + " x " + equipmentType.getName() + " for " + base.getName());
        return saved;
    }

    public List<Purchase> getPurchases(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        return purchaseRepository.filterPurchases(baseId, equipmentTypeId, startDate, endDate);
    }

    // --- Transfers ---
    @Transactional
    public Transfer createTransfer(TransferRequestDTO dto) {
        if (dto.getFromBaseId().equals(dto.getToBaseId())) {
            throw new IllegalArgumentException("Source and destination bases cannot be identical");
        }

        int availableStock = getAvailableStock(dto.getFromBaseId(), dto.getEquipmentTypeId());
        if (dto.getQuantity() > availableStock) {
            throw new InsufficientStockException("Insufficient stock at source base. Available: " + availableStock + ", requested: " + dto.getQuantity());
        }

        Base fromBase = baseRepository.findById(dto.getFromBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Source Base not found with ID: " + dto.getFromBaseId()));

        Base toBase = baseRepository.findById(dto.getToBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Base not found with ID: " + dto.getToBaseId()));

        EquipmentType equipmentType = equipmentTypeRepository.findById(dto.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment Type not found with ID: " + dto.getEquipmentTypeId()));

        Transfer transfer = Transfer.builder()
                .fromBase(fromBase)
                .toBase(toBase)
                .equipmentType(equipmentType)
                .quantity(dto.getQuantity())
                .date(dto.getDate())
                .build();

        Transfer saved = transferRepository.save(transfer);
        auditLogService.logAction(null, "RECORD_TRANSFER", "TRANSFER", saved.getId(),
                "Transferred " + saved.getQuantity() + " x " + equipmentType.getName() + " from " + fromBase.getName() + " to " + toBase.getName());
        return saved;
    }

    public List<Transfer> getTransfers(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        return transferRepository.filterTransfers(baseId, equipmentTypeId, startDate, endDate);
    }

    // --- Assignments ---
    @Transactional
    public Assignment createAssignment(AssignmentRequestDTO dto) {
        int availableStock = getAvailableStock(dto.getBaseId(), dto.getEquipmentTypeId());
        if (dto.getQuantity() > availableStock) {
            throw new InsufficientStockException("Insufficient stock at base. Available: " + availableStock + ", requested: " + dto.getQuantity());
        }

        Base base = baseRepository.findById(dto.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + dto.getBaseId()));

        EquipmentType equipmentType = equipmentTypeRepository.findById(dto.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment Type not found with ID: " + dto.getEquipmentTypeId()));

        Assignment assignment = Assignment.builder()
                .base(base)
                .equipmentType(equipmentType)
                .personnelName(dto.getPersonnelName())
                .quantity(dto.getQuantity())
                .date(dto.getDate())
                .build();

        Assignment saved = assignmentRepository.save(assignment);
        auditLogService.logAction(null, "RECORD_ASSIGNMENT", "ASSIGNMENT", saved.getId(),
                "Assigned " + saved.getQuantity() + " x " + equipmentType.getName() + " to " + dto.getPersonnelName() + " at " + base.getName());
        return saved;
    }

    public List<Assignment> getAssignments(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        return assignmentRepository.filterAssignments(baseId, equipmentTypeId, startDate, endDate);
    }

    // --- Expenditures ---
    @Transactional
    public Expenditure createExpenditure(ExpenditureRequestDTO dto) {
        int availableStock = getAvailableStock(dto.getBaseId(), dto.getEquipmentTypeId());
        if (dto.getQuantity() > availableStock) {
            throw new InsufficientStockException("Insufficient stock at base. Available: " + availableStock + ", requested: " + dto.getQuantity());
        }

        Base base = baseRepository.findById(dto.getBaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + dto.getBaseId()));

        EquipmentType equipmentType = equipmentTypeRepository.findById(dto.getEquipmentTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipment Type not found with ID: " + dto.getEquipmentTypeId()));

        Expenditure expenditure = Expenditure.builder()
                .base(base)
                .equipmentType(equipmentType)
                .reason(dto.getReason())
                .quantity(dto.getQuantity())
                .date(dto.getDate())
                .build();

        Expenditure saved = expenditureRepository.save(expenditure);
        auditLogService.logAction(null, "RECORD_EXPENDITURE", "EXPENDITURE", saved.getId(),
                "Expended " + saved.getQuantity() + " x " + equipmentType.getName() + " at " + base.getName() + " (Reason: " + dto.getReason() + ")");
        return saved;
    }

    public List<Expenditure> getExpenditures(Long baseId, Long equipmentTypeId, LocalDate startDate, LocalDate endDate) {
        return expenditureRepository.filterExpenditures(baseId, equipmentTypeId, startDate, endDate);
    }

    // --- Reference Data & Base Management ---
    public List<Base> getAllBases() {
        return baseRepository.findAll();
    }

    public Base createBase(Base base) {
        return baseRepository.save(base);
    }

    public Base updateBase(Long id, Base baseDetails) {
        Base base = baseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Base not found with ID: " + id));
        base.setName(baseDetails.getName());
        base.setLocation(baseDetails.getLocation());
        return baseRepository.save(base);
    }

    public void deleteBase(Long id) {
        if (!baseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Base not found with ID: " + id);
        }
        baseRepository.deleteById(id);
    }

    public List<EquipmentType> getAllEquipmentTypes() {
        return equipmentTypeRepository.findAll();
    }

    public List<PersonnelUnit> getAllPersonnelUnits() {
        return personnelUnitRepository.findAll();
    }
}
