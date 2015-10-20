/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen;

/**
 *
 * @author lucien
 */
public class MachineCreator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        AWS aws = AWS.getInstance();
        String s = aws.createMachine();
        System.out.println(s);
        aws.destroyMachine(s);
        
    }
    
}
