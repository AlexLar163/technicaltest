package com.alargo.account_movements_microservice.services;

import com.alargo.account_movements_microservice.repository.MovementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class ReportService {
    private final AccountService accountService;
    private final MovementRepository movementRepository;

    @Autowired
    public ReportService(AccountService accountService, MovementRepository movementRepository) {
        this.accountService = accountService;
        this.movementRepository = movementRepository;
    }

//    @Autowired
//    private ClRepository clienteRepository;

    public Map<String, Object> generarReporte(Date fechaInicio, Date fechaFin, Long clienteId) {
//        Map<String, Object> reporte = new HashMap<>();
//
//        // Obtener datos de las cuentas del cliente
//        reporte.put("cliente", customerRepository.findById(clienteId));
//        reporte.put("cuentas", accountService.findCuentasByClienteId(clienteId));
//
//        // Obtener movimientos entre el rango de fechas
//        reporte.put("movimientos", movimientoRepository.findMovimientosByClienteIdAndFechaBetween(clienteId, fechaInicio, fechaFin));
//
        return null;
    }
}
