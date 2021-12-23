package passwordsecuritychecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alessandro Castelli
 * @version 07.10.2021
 */
public class PasswordSecurityChecker {
    private String password;
    private List<String> arguments;
    private List<String> fileCsvRows;
    private long time;
    private long attemps;
    private boolean isPasswordFound;
    private final int MAX_PASSWORD_LENGTH = 4;
    
    
    public PasswordSecurityChecker(List arguments) throws IOException{
        this.password = arguments.get(0).toString();
        if(arguments.size() > 1){
            setArguments(arguments);
        }else{
            this.arguments = null;
        }
        this.fileCsvRows = new ArrayList<>();
        InputStream is = getClass().getResourceAsStream("/files/Most100kUsedPasswords.csv"); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String s;
        while ((s=reader.readLine())!= null){
            this.fileCsvRows.add(s);
        }
        this.time = 0;
        this.attemps = 0;
        this.isPasswordFound = false;
    }
    
    public void forcePassword(){
        if(this.password.length() > MAX_PASSWORD_LENGTH){
            System.out.print("Inserire una password con al massimo ");
            System.out.println(MAX_PASSWORD_LENGTH + " caratteri");
        }else{
            long startTime = System.currentTimeMillis() / 1000;
            if(this.arguments != null){
                checkArguments();
            }
            if(!isPasswordFound){
                checkCsv();
            }
            if(!isPasswordFound){
                bruteForce("");
            }
            long endTime = System.currentTimeMillis() / 1000;
            this.time = endTime - startTime;
            if(isPasswordFound){
                printForce();
            }else{
                System.out.print("\r");
                System.out.print("Passare una password con caratteri ");
                System.out.println("della tastiera francese svizzera");
            }
            
        }
    }
    
    public void setArguments(List<String> arguments){
        this.arguments = new ArrayList<>();
        
        int paramSize;
        if(arguments.size() > 5){
            paramSize = 5;
        }else{
            paramSize = arguments.size() - 1;
        }
        
        //Creao e riempio la lista params
        List<String> params = new ArrayList<>();
        for(int i = 1; i <= paramSize; i++){
            //normale
            params.add(arguments.get(i));
            //minuscolo
            params.add(arguments.get(i).toLowerCase());
            //maiuscolo
            params.add(arguments.get(i).toUpperCase());
        }
        
        for(int i = 0; i < params.size(); i++){
            if(params.get(i).length() > 3){
                params.add(params.get(i).substring(0, 3));
            }
            if(params.get(i).length() > 2){
                params.add(params.get(i).substring(0, 2));
            }
            if(params.get(i).length() > 1){
                params.add(params.get(i).substring(0, 1));
            }
        }
        
        //Aggiunta degli argomenti alla lista
        for(int i = 0; i < params.size(); i++){
            this.arguments.add(params.get(i));
        }
        
        //Aggiunta mischiando gli argomenti con tutte le combinazioni
        //miscugli con 2 e 3 elementi
        for(int i = 0; i < params.size(); i++){
            for(int j = 0; j < params.size(); j++){
                this.arguments.add(params.get(i) + params.get(j));
                for(int k = 0; k < params.size(); k++){
                    this.arguments.add(params.get(i) + params.get(j) + params.get(k));
                }
            }
        }
        
        //Aggiunta dividendo la data di nascita in giorni, mesi e anni
        //mischiando questi valori con il nome e il cognome
        if(params.size() >= 7){
            String[] splitDate = params.get(7).split("[.]", 0);
            if(splitDate.length == 3){
                for(int i = 0; i < splitDate.length; i++){
                    for(int j = 0; j < params.size(); j++){ 
                        this.arguments.add(splitDate[i] + params.get(j));
                        this.arguments.add(params.get(j) + splitDate[i]);
                        for(int k = 0; k < params.size(); k++){
                            this.arguments.add(params.get(j) + splitDate[i] + params.get(k));
                            this.arguments.add(params.get(j) + params.get(k) + splitDate[i]);
                            this.arguments.add(splitDate[i] + params.get(j) + params.get(k));
                        }
                    }
                }
            }
        }
    }
    
    public void checkArguments(){
        for(int i = 0; i < arguments.size(); i++){
            if(!isPasswordFound){
                attemps++;
                if(password.equals(arguments.get(i))){
                    isPasswordFound = true;
                    break;
                }
            }
        }
    } 
    
    public void checkCsv(){
        for(int i = 0; i < fileCsvRows.size(); i++){
            if(!isPasswordFound){
                attemps++;
                if(password.equals(fileCsvRows.get(i))){
                    isPasswordFound = true;
                    break;
                }
            }
        }
    }
    
    public void bruteForce(String pass){
        if(pass.length() < MAX_PASSWORD_LENGTH){
            for(char c = '!'; c <= '°'; c++){
                if(!isPasswordFound){
                    attemps++;
                    if(password.equals(pass + Character.toString(c))){
                        isPasswordFound = true;
                        break;
                    }else{
                        if(attemps % 100000000 == 0){
                            System.out.print("\rCercando...   Tentativi: " + attemps );
                        }
                        bruteForce(pass + Character.toString(c));
                    } 
                }
            }
        }
    }
    
    public void printForce(){
        System.out.print("\r");
        System.out.print("La password (" + password + ") è stata trovata con ");
        System.out.println(attemps + " tentativi in " + time + " secondi");
    }
    
    public static void main(String[] args) {
        String usage = "Avvia il programma passando i seguenti argomenti"
                + " nel seguente ordine:"
                + "\nla password"
                + "\nil tuo nome"
                + "\nil tuo cognome"
                + "\nla tua data di nascita (nel formato dd.mm.yyyy)"
                + "\ne se vuoi un argomento in aggiunta";
        if(args.length > 0){
            try{
                List<String> arguments = new ArrayList<>();
                arguments.addAll(Arrays.asList(args));
                PasswordSecurityChecker psc = new PasswordSecurityChecker(arguments);
                psc.forcePassword();
            }catch(IOException e){
                System.out.println("Errore nella lettura del file");
            }
        }else{
            System.out.println(usage);
        }
    }
}