package calculator.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class CalculatorController {
    // http://localhost:8080/test?personalCode=6&requestedAmount=425342&requestedPeriod=12
    @CrossOrigin
    @GetMapping("test")
    public String loanCalculator (@RequestParam("personalCode") String personalCode,
                                  @RequestParam("requestedAmount") int requestedAmount,
                                  @RequestParam("requestedPeriod") int requestedPeriod){
        if (personalCode.equals("49002010965")){
            return "Sinu laenuotsus on negatiivne.";
        }
        StringBuilder vastus = new StringBuilder();
        try {
            int creditModifier = creditModifierFunction(personalCode);
            double loanAmount = maxLoanAmount(creditModifier, requestedPeriod);
            if (loanAmount < 2000 || loanAmount < requestedAmount) {
                vastus.append("Sinu laenuotsus on negatiivne.").append("<br>");
            } else {
                vastus.append("Sinu laenuotsus on positiivne!<br>");
                if (loanAmount > 10000) {
                    loanAmount = 10000;}
                vastus.append("Saame sulle laenata kuni " + (int) loanAmount + " eur sinu küsitud perioodiks (" + requestedPeriod + "ks kuuks).<br>");
            }
            //OPTION 2-> KÜSITUD SUMMA MIN KUUDEKS
            int newLoanPeriod = newLoanPeriodFuncion(requestedAmount, creditModifier);
            if (newLoanPeriod <= 60 && newLoanPeriod >= 12 && newLoanPeriod != requestedPeriod) {
                vastus.append("Sinu küsitud simmat ("+requestedAmount+") saame laenata perioodiga alates " + newLoanPeriod + " kuud.<br>");
            }

            //OPTION 3 -> ALATES MILLISEST KUST SAAME PAKKUDA 2000
            if(requestedAmount != 2000) {
                double minAmount = 2000;
                int minPeriodwithMinimumAmount = newLoanPeriodFuncion(2000, creditModifier);
                if (minPeriodwithMinimumAmount < 12) {
                    minAmount = maxLoanAmount ( creditModifier, 12);
                    minPeriodwithMinimumAmount = 12;
                }
                vastus.append("VÕI saame pakkuda Sulle laenu alates "+(int) minAmount+" eur laenuperioodiga alates " + minPeriodwithMinimumAmount + " kuud <br>");

            }
            //OPTION 4 MILLIST SUMMAT SAAME PAKKUDA 60KS KUUKS
            int maxkuud = 60;
            int maxLoanAmountwithMaxPeriod = (int) maxLoanAmount(creditModifier, 60);
            if (maxLoanAmountwithMaxPeriod > 10000) {
                maxLoanAmountwithMaxPeriod = 10000;
                maxkuud = newLoanPeriodFuncion(maxLoanAmountwithMaxPeriod, creditModifier);
            }
            if (maxLoanAmountwithMaxPeriod != requestedAmount && requestedPeriod !=60) {
                vastus.append("VÕI saame pakkuda Sulle laenu summas " + maxLoanAmountwithMaxPeriod + " eur laenuperioodiga "+maxkuud+" kuud.<br>");
            }
        } catch (IllegalStateException e) {
            return "Palun kontroolli oma isikukoodi: " + personalCode;
        }
        if (requestedPeriod < 12 || requestedPeriod > 60) {
            return "Meie laenu periood on vahemikus 12 - 60 kuud. Vali omale sobiv.";
        }

        return vastus.toString();
    }

    public static int creditModifierFunction (String personalCode){
        switch (personalCode) {
            case "49002010976": {
                return 100;
            }
            case "49002010987": {
                return 300;
            }
            case "49002010998": {
                return 1000;
            }
            default:
                throw new IllegalStateException("Palun kontroolli oma personaalkoodi: " + personalCode);
        }
    }
    public static double maxLoanAmount ( int creditModifier, int loanPeriod){
        return creditModifier / (1.0 / loanPeriod);
    }
    public static int newLoanPeriodFuncion ( double requestedAmount, int creditModifier){
        return (int) requestedAmount / creditModifier;
    }

}