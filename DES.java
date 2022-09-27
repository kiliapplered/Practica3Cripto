public class DES {

    int[] clave;
    int[] texto;
    int[][] S0={ { 1, 0, 3, 2 },
                 { 3, 2, 1, 0 },
                 { 0, 2, 1, 3 },
                 { 3, 1, 3, 2 } };
    int[][] S1={ { 0, 1, 2, 3 },
                 { 2, 0, 1, 3 },
                 { 3, 0, 1, 0 },
                 { 2, 1, 0, 3 } };
    int[] subKey1;
    int[] subKey2;

    public DES(String claveCadena, String textoCadena){
        this.clave=llenadoArreglos(claveCadena);
        this.texto=llenadoArreglos(textoCadena);
        //imprimirArreglo(clave);
        //imprimirArreglo(texto);
    }

    public int[] llenadoArreglos(String cadena){
        int[] aux=new int[cadena.length()];
        for(int i=0; i<cadena.length(); i++){
            aux[i]=(int)cadena.charAt(i);
            if(aux[i]==48){
                aux[i]=0;
            } else {
                aux[i]=1;
            }
        }
        return aux;
    }

    public void imprimirArreglo(int[] arreglo){
        for(int i=0; i<arreglo.length; i++){
            System.out.print(arreglo[i]+" ");
        }
        System.out.println("");
    }

    public int[] permutacion(int[] arregloPerm, int[] arregloPos){
        int[] aux=new int[arregloPos.length];
        for(int i=0; i<arregloPos.length; i++){
            aux[i]=arregloPerm[arregloPos[i]];
        }
        return aux;
    }

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

    public int[] union(int[] arreglo1, int[] arreglo2){
        int[] aux=new int[arreglo1.length*2];
        for(int i=0; i<arreglo1.length; i++){
            aux[i]=arreglo1[i];
            aux[i+(arreglo1.length)]=arreglo2[i];
            //System.out.println(arreglo1[i]+ " "+arreglo2[i]);
        }
        return aux;
    }

    public void obtencionSubkeys(){
        // Creación de las subclaves
        // Paso 1: Permutación
        int[] arregloPos1={2,4,1,6,3,9,0,8,7,5};
        int[] arregloPermutado1=permutacion(clave, arregloPos1);
        //imprimirArreglo(arregloPermutado1);
        // Paso 2: División
        int[] sub1=division(arregloPermutado1, 1);
        int[] sub2=division(arregloPermutado1, 2);
        //imprimirArreglo(sub1);
        //imprimirArreglo(sub2);
        // Paso 3: Desplazamiento de bit a la izquierda
        sub1=desplazamiento(sub1, 1);
        sub2=desplazamiento(sub2, 1);
        //imprimirArreglo(sub1);
        //imprimirArreglo(sub2);
        // Paso 4: Se unen las 2 cadenas anteriores
        int[] preKey1=union(sub1, sub2);
        //imprimirArreglo(preKey1);
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
        //imprimirArreglo(subKey1);
        //imprimirArreglo(subKey2);
    }

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

    public void cifrado(){
        // Permutación inicial
        int[] arregloPos1={1,5,2,0,3,7,4,6};
        int[] arregloPermutado1=permutacion(this.texto, arregloPos1);
        //imprimirArreglo(arregloPermutado1);

        //Inicio Feistel
        int[] left=division(arregloPermutado1, 1);
        int[] right=division(arregloPermutado1, 2);
        //imprimirArreglo(left);
        //imprimirArreglo(right);

        // Mezcla de right con Subkey1
        // Expansion
        int[] arregloPos2={3,0,1,2,1,2,3,0};
        int[] expRight=permutacion(right, arregloPos2);
        //imprimirArreglo(expRight);
        // Aplicación de XOR con Subkey1
        int[] xor1=new int[expRight.length];
        for(int i=0; i<expRight.length; i++){
            xor1[i]=(expRight[i]^this.subKey1[i]);
        }
        //imprimirArreglo(xor1);
        // Division previa a S-Box
        int[] temp1=division(xor1, 1);
        int[] temp2=division(xor1, 2);
        //imprimirArreglo(temp1);
        //imprimirArreglo(temp2);

        //S-Box S0
        int row, col, val;
        row=Integer.parseInt(""+temp1[0]+temp1[3], 2);
        col=Integer.parseInt(""+temp1[1]+temp1[2], 2);
        val=S0[row][col];
        String val1=binario(val);
        int[] cad1=llenadoArreglos(val1);

        //S-Box S1
        row=Integer.parseInt(""+temp2[0]+temp2[3], 2);
        col=Integer.parseInt(""+temp2[1]+temp2[2], 2);
        val=S1[row][col];
        String val2=binario(val);
        int[] cad2=llenadoArreglos(val2);

        //Concatenación de resultados
        int[] concat=union(cad1, cad2);
        //imprimirArreglo(concat);
        
        //Permutacion ----------------------- 
        int[] arregloPos3={1,3,2,0};
        int[] perm1=permutacion(concat, arregloPos3);
        //imprimirArreglo(perm1);

        //XOR con la mitad izquierda
        int[] xor2=new int[perm1.length];
        for(int i=0; i<perm1.length; i++){
            xor2[i]=(perm1[i]^left[i]);
        }
        //imprimirArreglo(xor2);

        //Concatenación con mitad derecha
        int[] concat2=union(xor2, right);
        //imprimirArreglo(concat2);

        // Fin feistel

        //Intercambio de izquierda y derecha
        left=division(concat2, 2);
        right=division(concat2, 1);
        //imprimirArreglo(left);
        //imprimirArreglo(right);

        
        int[] expRight2=permutacion(right, arregloPos2);
        //imprimirArreglo(expRight2);
        // Aplicación de XOR con Subkey2
        //imprimirArreglo(subKey1);
        //imprimirArreglo(subKey2);
        int[] xor3=new int[expRight2.length];
        for(int i=0; i<expRight2.length; i++){
            xor3[i]=(expRight2[i]^this.subKey2[i]);
        }
        //imprimirArreglo(xor3);
        // Division previa a S-Box
        int[] temp3=division(xor3, 1);
        int[] temp4=division(xor3, 2);

        //S-Box S0
        row=Integer.parseInt(""+temp3[0]+temp3[3],2);
        col=Integer.parseInt(""+temp3[1]+temp3[2],2);
        val=S0[row][col];
        String val3=binario(val);
        int[] cad3=llenadoArreglos(val3);
        //imprimirArreglo(cad3);

        //S-Box S1
        row=Integer.parseInt(""+temp4[0]+temp4[3],2);
        col=Integer.parseInt(""+temp4[1]+temp4[2],2);
        val=S1[row][col];
        String val4=binario(val);
        int[] cad4=llenadoArreglos(val4);
        //imprimirArreglo(cad4);

        //Concatenación de resultados
        int[] concat3=union(cad3, cad4);
        //imprimirArreglo(concat3);
        
        //Permutacion ----------------------- 
        int[] perm2=permutacion(concat3, arregloPos3);
        //imprimirArreglo(perm2);

        //XOR con la mitad izquierda
        int[] xor4=new int[perm2.length];
        for(int i=0; i<perm2.length; i++){
            xor4[i]=(perm2[i]^left[i]);
        }
        //imprimirArreglo(xor4);

        //Concatenación con mitad derecha
        int[] concat4=union(xor4, right);
        //imprimirArreglo(concat4);

        //Permutacion final
        int[] arregloPerm={3,0,2,4,6,1,7,5};
        int[] cadenaFinal=permutacion(concat4, arregloPerm);

        imprimirArreglo(cadenaFinal);

    }
    
}