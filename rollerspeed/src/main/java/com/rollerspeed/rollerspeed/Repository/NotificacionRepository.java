package com.rollerspeed.rollerspeed.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rollerspeed.rollerspeed.Model.NotificacionModel;
import com.rollerspeed.rollerspeed.Model.UserModel;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionModel, Long>{

        List<NotificacionModel> findByUsuarioOrderByFechaDesc(UserModel usuario);
}
