package com.rollerspeed.rollerspeed.Service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rollerspeed.rollerspeed.Model.NotificacionModel;
import com.rollerspeed.rollerspeed.Model.UserModel;
import com.rollerspeed.rollerspeed.Repository.NotificacionRepository;

@Service
public class NotificacionService {
     @Autowired
    private NotificacionRepository notificacionRepository;

    public void crearNotificacion(UserModel usuario, String mensaje) {
        NotificacionModel notificacion = new NotificacionModel();
        notificacion.setUsuario(usuario);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFecha(LocalDateTime.now());
        notificacionRepository.save(notificacion);
    }
}
