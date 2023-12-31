package Mauro.Salernoflix.service;

import Mauro.Salernoflix.dto.Enum.Role;
import Mauro.Salernoflix.dto.Requests.AnagraficaUtenteRequest;
import Mauro.Salernoflix.model.AnagraficaUtente;
import Mauro.Salernoflix.model.User;
import Mauro.Salernoflix.repository.AnagraficaUtenteRepository;
import Mauro.Salernoflix.repository.UserRepository;
import Mauro.Salernoflix.security.SalSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AnagraficaUtenteRepository anagraficaUtenteRepository;

    public List<User> getAll(int pageSize, int pageNumber, Role role) {

        return userRepository.findAll().stream()
            .filter(user -> user.getRole().equals(role))
            .skip((long) pageSize * pageNumber)
            .limit(pageSize)
            .toList();
    }

    public AnagraficaUtente creaAnagraficaUtente(AnagraficaUtenteRequest anagraficaUtenteRequest) {
        if (Objects.isNull(SalSecurityContext.getPrincipal().getAnagraficaUtente())) {
            User user = userRepository.findByUsername(SalSecurityContext.getPrincipal().getUsername());
            AnagraficaUtente anagraficaUtente = AnagraficaUtente.builder()
                .nome(anagraficaUtenteRequest.getNome())
                .cognome(anagraficaUtenteRequest.getCognome())
                .citta(anagraficaUtenteRequest.getCitta())
                .codiceFiscale(anagraficaUtenteRequest.getCodiceFiscale())
                .indirizzo(anagraficaUtenteRequest.getIndirizzo())
                .email(anagraficaUtenteRequest.getEmail())
                .cellulare(anagraficaUtenteRequest.getCellulare())
                .build();
            anagraficaUtenteRepository.save(anagraficaUtente);
            user.setAnagraficaUtente(anagraficaUtente);
            userRepository.save(user);
            return user.getAnagraficaUtente();
        }
        throw new RuntimeException("Anagrafica utente già presente");
    }

    public AnagraficaUtente getAnagraficaUtente() {
        return Objects.nonNull(SalSecurityContext.getPrincipal().getAnagraficaUtente())
            ? SalSecurityContext.getPrincipal().getAnagraficaUtente()
            : null;
    }

    public AnagraficaUtente patchAnagraficaUtenteLoggato(HashMap<String, Object> request) {
        AnagraficaUtente anagraficaUtente = anagraficaUtenteRepository.findById(SalSecurityContext.getPrincipal().getAnagraficaUtente().getId()).orElseThrow(
            () -> new RuntimeException("Anagrafica utente non trovata"));
        request.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(AnagraficaUtente.class, key);
            field.setAccessible(true);
            ReflectionUtils.setField(field, anagraficaUtente, value);
        });
        return anagraficaUtenteRepository.save(anagraficaUtente);
    }

    public AnagraficaUtente getAnagraficaUtenteById(Long idUser) {
        User user = userRepository.findById(idUser)
            .orElseThrow(() -> new RuntimeException("User non trovato."));
        if (Objects.nonNull(user.getAnagraficaUtente()))
            return user.getAnagraficaUtente();
        else
            throw new RuntimeException("L'utente non ha inserito un anagrafica utente.");
    }

}
