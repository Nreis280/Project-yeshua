CREATE TABLE empresas(
    id INT PRIMARY KEY UNIQUE NOT NULL,  
    razaoSocial VARCHAR(255) UNIQUE NOT NULL,
    cnpj VARCHAR(100) UNIQUE NOT NULL,        
    apelido VARCHAR(100) UNIQUE NOT NULL,             
    status VARCHAR(50) NOT NULL,                 
    cidade VARCHAR(100) NOT NULL,                
    porte VARCHAR(50) NOT NULL,                   
    cdAcessoSN VARCHAR(100),            
    simplesNacional VARCHAR(10),        
    declaracoes VARCHAR(50) NOT NULL,              
    postoFiscal VARCHAR(100),        
    senhaPostoFiscal VARCHAR(100),     
    ccm VARCHAR(20),                     
    senhaGiss VARCHAR(100),              
    userNFE VARCHAR(100),                
    senhaNFE VARCHAR(100)                
);
