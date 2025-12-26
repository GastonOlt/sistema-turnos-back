package com.gaston.sistema.turno.sistematunos_back.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gaston.sistema.turno.sistematunos_back.dto.ReseniaRequestDTO;
import com.gaston.sistema.turno.sistematunos_back.dto.ReseniaResponseDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.EstadoTurno;
import com.gaston.sistema.turno.sistematunos_back.entities.Local;
import com.gaston.sistema.turno.sistematunos_back.entities.Resenia;
import com.gaston.sistema.turno.sistematunos_back.entities.Turno;
import com.gaston.sistema.turno.sistematunos_back.repositories.LocalRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.ReseniaRepository;
import com.gaston.sistema.turno.sistematunos_back.repositories.TurnoRepository;

@Service
public class ReseniaServiceImp implements ReseniaService {

    private final ReseniaRepository reseniaRepository;
    private final TurnoRepository turnoRepository;
    private final LocalRepository localRepository;

    public ReseniaServiceImp(ReseniaRepository reseniaRepository, TurnoRepository turnoRepository, LocalRepository localRepository) {
        this.reseniaRepository = reseniaRepository;
        this.turnoRepository = turnoRepository;
        this.localRepository = localRepository;
    }

    @Override
    @Transactional
    public ReseniaResponseDTO publicarResenia(Long clienteId, ReseniaRequestDTO request) {
        Turno turnoActual = turnoRepository.findById(request.getTurnoId())
                .orElseThrow(() -> new IllegalArgumentException("El turno no existe"));

        if (!turnoActual.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("Este turno no corresponde al cliente autenticado");
        }

        if (turnoActual.getEstado() != EstadoTurno.FINALIZADO) {
            throw new IllegalArgumentException("Solo se pueden reseñar turnos FINALIZADOS");
        }

        Long localId = turnoActual.getLocal().getId();

        Optional<Resenia> reseniaExistente = reseniaRepository.findByLocalIdAndClienteId(localId, clienteId);

        Resenia resenia;
        if (reseniaExistente.isPresent()) {
            resenia = reseniaExistente.get();
            resenia.setCalificacion(request.getCalificacion());
            resenia.setComentario(request.getComentario());
            
            resenia.setTurno(turnoActual); 
        } else {
            resenia = new Resenia();
            resenia.setCalificacion(request.getCalificacion());
            resenia.setComentario(request.getComentario());
            resenia.setLocal(turnoActual.getLocal());
            resenia.setCliente(turnoActual.getCliente());
            resenia.setTurno(turnoActual);
        }

        Resenia reseniaGuardada = reseniaRepository.save(resenia);
        actualizarPromedioLocal(reseniaGuardada.getLocal().getId());

        return convertirADTO(reseniaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReseniaResponseDTO> obtenerReseniasPorLocal(Long localId) {
        return reseniaRepository.findByLocalIdOrderByFechaUltimaModificacionDesc(localId).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double obtenerPromedioLocal(Long localId) {
        Double promedio = reseniaRepository.obtenerPromedioCalificacion(localId);
        return promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0;
    }

    private void actualizarPromedioLocal(Long localId) {
        Double nuevoPromedio = reseniaRepository.obtenerPromedioCalificacion(localId);
        if (nuevoPromedio == null) nuevoPromedio = 0.0;
        
        nuevoPromedio = Math.round(nuevoPromedio * 10.0) / 10.0;

        Local local = localRepository.findById(localId).orElseThrow();
        local.setPromedioCalificacion(nuevoPromedio);
        localRepository.save(local);
    }
    

    private ReseniaResponseDTO convertirADTO(Resenia r) {
        String nombreCliente = (r.getCliente() != null) 
            ? r.getCliente().getNombre() + " " + r.getCliente().getApellido()
            : "Anónimo";
        
        return new ReseniaResponseDTO(
            r.getId(),
            r.getCalificacion(),
            r.getComentario(),
            nombreCliente,
            r.getFechaUltimaModificacion(),
            r.getTurno().getServicio().getNombre()
        );
    }
}
