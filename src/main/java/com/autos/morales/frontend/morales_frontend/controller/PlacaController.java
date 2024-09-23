package com.autos.morales.frontend.morales_frontend.controller;

import com.autos.morales.frontend.morales_frontend.dto.PlacaRequestDTO;
import com.autos.morales.frontend.morales_frontend.dto.PlacaResponseDTO;
import com.autos.morales.frontend.morales_frontend.viewmodel.PlacaModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/placa")
public class PlacaController {

    private final RestTemplate restTemplate;

    public PlacaController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/buscar")
    public String buscar(Model model) {
        PlacaModel placaModel = new PlacaModel("00", "", "", "", 0, 0.0, "");
        model.addAttribute("placaModel", placaModel);
        return "buscar";
    }

    @PostMapping("/validar")
    public String validar(@RequestParam("placa") String placa, Model model) {

        if (placa == null || placa.trim().isEmpty() || !placa.matches("^[A-Z0-9]{3}-[0-9]{3}$")) {
            PlacaModel placaModel = new PlacaModel("01", "Debe ingresar una placa correcta", "", "", 0, 0.0, "");
            model.addAttribute("placaModel", placaModel);
            return "buscar";
        }

        try {
            // Invocar API de búsqueda de vehículo
            String endpoint = "http://localhost:8084/api/vehiculo/buscar";
            PlacaRequestDTO placaRequestDTO = new PlacaRequestDTO(placa);
            PlacaResponseDTO placaResponseDTO = restTemplate.postForObject(endpoint, placaRequestDTO, PlacaResponseDTO.class);

            // Validar respuesta del backend
            if (placaResponseDTO.codigo().equals("00")) {
                PlacaModel placaModel = new PlacaModel("00", "", placaResponseDTO.marca(),
                        placaResponseDTO.modelo(), placaResponseDTO.numeroAsientos(),
                        placaResponseDTO.precio(), placaResponseDTO.color());
                model.addAttribute("placaModel", placaModel);
                return "resultado";
            } else {
                PlacaModel placaModel = new PlacaModel("02", "No se encontró un vehículo para la placa ingresada", "", "", 0, 0.0, "");
                model.addAttribute("placaModel", placaModel);
                return "buscar"; // si no se encuentra
            }

        } catch (Exception e) {
            PlacaModel placaModel = new PlacaModel("99", "Ocurrió un error en la búsqueda del vehículo", "", "", 0, 0.0, "");
            model.addAttribute("placaModel", placaModel);
            System.out.println(e.getMessage());
            return "buscar";
        }
    }
}
