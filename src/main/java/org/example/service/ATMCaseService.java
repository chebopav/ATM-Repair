package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.model.ATMCase;
import org.example.repository.ATMCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ATMCaseService {

    @Autowired
    private ATMCaseRepository repository;

    public Integer readFile(List<JsonNode> nodes) {
        List<ATMCase> atmCases = transformNodesToCases(nodes);
        repository.saveAll(atmCases);
        return atmCases.size();
    }

    public List<ATMCase> getAllCases() {
        return repository.findAll();
    }

    public Map<String, List<ATMCase>> getTopThreeCauses() {
        List<ATMCase> allCases = repository.findAll();
        Map<String, List<ATMCase>> map = allCases.stream().collect(Collectors.groupingBy(ATMCase::getCause));
        return map.entrySet().stream().sorted(((o1, o2) -> Integer.compare(o2.getValue().size(), o1.getValue().size()))).limit(3).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public List<ATMCase> getTopThreeLength() {
        List<ATMCase> allCases = repository.findAll();
        Map<Long, List<ATMCase>> groupedByRepairingLength = new HashMap<>();
        allCases.forEach(atmCase -> {
            long diff = ChronoUnit.MINUTES.between(atmCase.getDateStart(), atmCase.getDateEnd());
            if (groupedByRepairingLength.containsKey(diff)) {
                groupedByRepairingLength.get(diff).add(atmCase);
            } else {
                groupedByRepairingLength.put(diff, new ArrayList<>(Collections.singletonList(atmCase)));
            }
        });

        List<ATMCase> result = new ArrayList<>();

        groupedByRepairingLength.entrySet().stream()
                .sorted((o1, o2) -> Long.compare(o2.getKey(), o1.getKey()))
                .limit(3)
                .map(Map.Entry::getValue)
                .collect(Collectors.toList())
                .forEach(result::addAll);

        return result;
    }

    public Map<String, List<ATMCase>> getDuplicates() {
        List<ATMCase> allCases = repository.findAll();
        Map<String, List<ATMCase>> groupedByATMId = allCases.stream().sorted(Comparator.comparing(ATMCase::getDateStart)).collect(Collectors.groupingBy(ATMCase::getAtmId));
        Map<String, List<ATMCase>> result = new HashMap<>();
        groupedByATMId.entrySet().stream().filter(entry -> entry.getValue().size() > 1).forEach(entry -> {
            List<ATMCase> cases = getCasesByCause(entry.getValue());
            if (!cases.isEmpty()) {
                result.put(entry.getKey(), cases);
            }
        });
        return result;
    }

    public Integer deleteAll() {
        int result = (int) repository.count();
        repository.deleteAll();
        return result;
    }

    private List<ATMCase> getCasesByCause(List<ATMCase> cases) {
        List<ATMCase> list = new ArrayList<>();
        cases.stream().collect(Collectors.groupingBy(ATMCase::getCause)).forEach((cause, atmCases) -> {
            atmCases.sort(Comparator.comparing(ATMCase::getDateStart));
            for (int i = 1; i < atmCases.size(); i++) {
                if (ChronoUnit.DAYS.between(atmCases.get(i - 1).getDateStart(), atmCases.get(i).getDateStart()) <= 15) {
                    list.add(atmCases.get(i - 1));
                    list.add(atmCases.get(i));
                }
            }
        });
        return list.stream().distinct().collect(Collectors.toList());
    }

    private List<ATMCase> transformNodesToCases(List<JsonNode> nodes) {
        List<ATMCase> cases = new ArrayList<>();
        try {
            for (JsonNode node : nodes) {
                ATMCase atmCase = ATMCase.builder()
                        .caseId(node.get("CASE ID").asInt())
                        .atmId(node.get("ATM ID").asText())
                        .cause(node.get("Название управляющей причины").asText())
                        .dateStart(getDateTimeFromStr(node.get("Начало").asText()))
                        .dateEnd(getDateTimeFromStr(node.get("Окончание").asText()))
                        .serialNumber(node.get("Серийный номер").asText())
                        .bankName(node.get("BANK_NM").asText())
                        .channel(node.get("Канал связи").asText())
                        .build();
                cases.add(atmCase);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка чтения файла", e);
        }
        return cases;
    }

    private LocalDateTime getDateTimeFromStr(String input) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("M/d/yy HH:mm");
        return LocalDateTime.ofInstant(format.parse(input).toInstant(), ZoneId.systemDefault());
    }
}


