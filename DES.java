/* 
 * @Autor Najera Noyola Karla Andrea
 * @Fecha 27 de septiembre de 2022
 * @Descripción Clase que permite el cifrado/descifrado mediante DES.
*/

public class DES {

    int[] clave;    // Clave con la que el texto será cifrada. 
    int[] texto;    // Texto a cifrar o descifrar.
    int[] subKey1;  // Subllave generada 1.
    int[] subKey2;  // Subllave generada 2.
    // S-Boxes.
    int[][] S0={ { 1, 0, 3, 2 },
                 { 3, 2, 1, 0 },
                 { 0, 2, 1, 3 },
                 { 3, 1, 3, 2 } };
    int[][] S1={ { 0, 1, 2, 3 },
                 { 2, 0, 1, 3 },
                 { 3, 0, 1, 0 },
                 { 2, 1, 0, 3 } };

    /* 
     * Constructor de la clase.
     * Recibe la clave y el texto que se cifrará/descifrará.
     */
    public DES(String claveCadena, String textoCadena){
        this.clave=llenadoArreglos(claveCadena);
        this.texto=llenadoArreglos(textoCadena);
    }

    /* 
     * Función de llenado de arreglos.
     * Recibe una cadena y convierte a arreglo de enteros el contenido. 
     */
    public int[] llenadoArreglos(String cadena){
        int[] aux=new int[cadena.length()];
        for(int i=0; i<cadena.length(); i++){
            aux[i]=Integer.parseInt(""+cadena.charAt(i));
        }
        return aux;
    }

    /* 
     * Función de impresión de arreglos.
     * Recibe un arreglo e imprime el contenido mediante un ciclo. 
     */
    public void imprimirArreglo(int[] arreglo){
        for(int i=0; i<arreglo.length; i++){
            System.out.print(arreglo[i]);
        }
        System.out.println("");
    }

    /* 
     * Permutador de arreglos.
     * Recibe un arreglo y lo acomoda según lo indicado en otro arreglo. 
     */
    public int[] permutacion(int[] arregloPerm, int[] arregloPos){
        int[] aux=new int[arregloPos.length];
        for(int i=0; i<arregloPos.length; i++){
            aux[i]=arregloPerm[arregloPos[i]];
        }
        return aux;
    }

    /* 
     * Divisor de arreglos.
     * Recibe un arreglo y lo divide a la mitad, devolviendo la parte indicada. 
     */
    public int[] division(int[] arreglo, int parte){
        int aux[]=new int[arreglo.length/2];
        int n=0;
        if(parte==2){ // Si se trata de la segunda parte de la división
            n=arreglo.length/2;
        } 
        for(int i=0; i<arreglo.length/2; i++){
            aux[i]=arreglo[i+n];
        }
        return aux;
    }

    /* 
     * Desplazador de arreglos.
     * Recibe un arreglo y lo mueve n posiciones como le sea indicado. 
     */
    public int[] desplazamiento(int[] arreglo, int pos){
        while(pos>0){
            int temp=arreglo[0];
            for(int i=0; i<arreglo.length-1; i++){
                arreglo[i]=arreglo[i+1];
            }
            arreglo[arreglo.length-1]=temp;
            pos--;
        }
        return arreglo;
    }

    /* 
     * Unión de arreglos.
     * Recibe 2 arreglos y los une en uno mismo. 
     */
    public int[] union(int[] arreglo1, int[] arreglo2){
        int[] aux=new int[arreglo1.length*2];
        for(int i=0; i<arreglo1.length; i++){
            aux[i]=arreglo1[i];
            aux[i+(arreglo1.length)]=arreglo2[i];
        }
        return aux;
    }

    /* 
     * Transformador de valores binarios.
     * Recibe un entero y lo transforma a una cadena binaria. 
     */
    public String binario(int val){
        if (val == 0)
            return "00";
        else if (val == 1)
            return "01";
        else if (val == 2)
            return "10";
        else
            return "11";
    }

    /* 
     * Obtención de subkeys.
     * Realiza la obtención de las subkeys a partir de la key proporcionada por el usuario. 
     */
    public void obtencionSubkeys(){

        // Paso 1: Permutación
        int[] arregloPos1={2,4,1,6,3,9,0,8,7,5};
        int[] arregloPermutado1=permutacion(clave, arregloPos1);

        // Paso 2: División
        int[] sub1=division(arregloPermutado1, 1);
        int[] sub2=division(arregloPermutado1, 2);

        // Paso 3: Desplazamiento de bit a la izquierda
        sub1=desplazamiento(sub1, 1);
        sub2=desplazamiento(sub2, 1);

        // Paso 4: Se unen las 2 cadenas anteriores
        int[] preKey1=union(sub1, sub2);

        // Paso 5: Se obtiene la subKey1
        int[] arregloPos2={5,2,6,3,7,4,9,8};
        this.subKey1=permutacion(preKey1, arregloPos2);

        // Paso 6: Desplazamiento de 2 bits a la izquierda del paso 3
        sub1=desplazamiento(sub1, 2);
        sub2=desplazamiento(sub2, 2);

        // Paso 7: Se unen las 2 cadenas anteriores
        int[] preKey2=union(sub1, sub2);

        // Paso 8: Se obtiene la subKey2
        this.subKey2=permutacion(preKey2, arregloPos2);

        // Se ha completado el proceso de obtención de subllaves
    }

    /* 
     * Obtención de subkeys.
     * Realiza la aplicación de la función feistel. 
     */
    public int[] feistel(int[] arregloInicial, int sk, int vf){

        // Se divide en secciones left y right 
        int[] left=division(arregloInicial, 1);
        int[] right=division(arregloInicial, 2);

        // Mezcla de right con Subkey1
        // Expansion de Right
        int[] arregloPos2={3,0,1,2,1,2,3,0};
        int[] expRight=permutacion(right, arregloPos2);

        // Aplicación de XOR con Subkey correspondiente
        int[] xor1=new int[expRight.length];
        if(sk==1){
            for(int i=0; i<expRight.length; i++){
                xor1[i]=(expRight[i]^this.subKey1[i]);
            }
        } else {
            for(int i=0; i<expRight.length; i++){
                xor1[i]=(expRight[i]^this.subKey2[i]);
            }
        }

        // Division previa a S-Box
        int[] temp1=division(xor1, 1);
        int[] temp2=division(xor1, 2);

        // S-Box S0
        int row, col, val;
        row=Integer.parseInt(""+temp1[0]+temp1[3], 2);
        col=Integer.parseInt(""+temp1[1]+temp1[2], 2);
        val=S0[row][col];
        String val1=binario(val);
        int[] cad1=llenadoArreglos(val1);

        // S-Box S1
        row=Integer.parseInt(""+temp2[0]+temp2[3], 2);
        col=Integer.parseInt(""+temp2[1]+temp2[2], 2);
        val=S1[row][col];
        String val2=binario(val);
        int[] cad2=llenadoArreglos(val2);

        // Concatenación de resultados
        int[] concat=union(cad1, cad2);
        
        // Permutacion
        int[] arregloPos3={1,3,2,0};
        int[] perm1=permutacion(concat, arregloPos3);

        // XOR con la mitad izquierda
        int[] xor2=new int[perm1.length];
        for(int i=0; i<perm1.length; i++){
            xor2[i]=(perm1[i]^left[i]);
        }

        // Concatenación con mitad derecha
        int[] concat2=new int[xor2.length*2];
        if(vf==1){
            concat2=union(right, xor2);
        } else {
            concat2=union(xor2, right);
        }
        return concat2;
    }

    public int[] cifrado(){

        // Obtención de subKeys
        obtencionSubkeys();

        // Permutación inicial
        int[] arregloPos1={1,5,2,0,3,7,4,6};
        int[] arregloPermutado1=permutacion(this.texto, arregloPos1);

        //  Aplicación de Feistel
        int[] concat=feistel(arregloPermutado1, 1, 1);

        // Intercambio
        int[] aux1=division(concat, 1);
        int[] aux2=division(concat, 2);
        
        // Concatenación
        concat=union(aux1, aux2);

        //  Aplicación de Feistel
        int[] concat2=feistel(concat, 2, 2);

        // Permutacion final
        int[] arregloPerm={3,0,2,4,6,1,7,5};
        int[] cadenaFinal=permutacion(concat2, arregloPerm);

        // Se devuelve el mensaje cifrado
        return cadenaFinal;
    }

    public int[] descifrado(){
        // Obtención de subKeys
        obtencionSubkeys();
        // Permutación inicial
        int[] arregloPos1={1,5,2,0,3,7,4,6};
        int[] arregloPermutado1=permutacion(this.texto, arregloPos1);

        // Aplicación de Feistel
        int[] concat2=feistel(arregloPermutado1, 2, 1);

        // Intercambio
        int[] aux1=division(concat2, 1);
        int[] aux2=division(concat2, 2);

        // Concatenación
        concat2=union(aux1, aux2);

        //  Aplicación de Feistel
        int[] concat4=feistel(concat2, 1, 2);

        // Permutacion final
        int[] arregloPerm={3,0,2,4,6,1,7,5};
        int[] cadenaFinal=permutacion(concat4, arregloPerm);

        // Se devuelve el mensaje descifrado
        return cadenaFinal;
    }
    
}
