-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- Host: johnny.heliohost.org
-- Generation Time: 01-Abr-2020 às 12:06
-- Versão do servidor: 5.7.29
-- versão do PHP: 7.2.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `dctidata_g12`
--

-- --------------------------------------------------------

--
-- Estrutura da tabela `logalerta`
--

CREATE TABLE `logalerta` (
  `idlogAlerta` int(11) NOT NULL,
  `IDAlerta` int(11) NOT NULL,
  `TipoAlerta` varchar(3) NOT NULL,
  `DataHoraAlerta` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `IDMedicao` int(11) NOT NULL,
  `ValorMedicao` decimal(6,2) NOT NULL,
  `TipoSensor` varchar(3) NOT NULL,
  `DataHoraMedicao` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `EmailUtilizadorConsultor` varchar(100) NOT NULL,
  `NomeUtilizadorConsultor` varchar(200) NOT NULL,
  `TipoUtilizadorConsultor` varchar(3) NOT NULL,
  `Data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Comando` varchar(200) NOT NULL,
  `Resultado` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estrutura da tabela `logcartao`
--

CREATE TABLE `logcartao` (
  `idlogCartao` int(11) NOT NULL,
  `idCartao` varchar(20) NOT NULL,
  `Ativo` tinyint(4) NOT NULL,
  `EmailUtilizador` varchar(100) NOT NULL,
  `NomeUtilizador` varchar(200) NOT NULL,
  `TipoUtilizador` varchar(3) NOT NULL,
  `EmailUtilizadorConsultor` varchar(100) NOT NULL,
  `NomeUtilizadorConsultor` varchar(200) NOT NULL,
  `TipoUtilizadorConsultor` varchar(3) NOT NULL,
  `Data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Comando` varchar(200) NOT NULL,
  `Resultado` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estrutura da tabela `logmedicaosensores`
--

CREATE TABLE `logmedicaosensores` (
  `idLogMedicao` int(11) NOT NULL,
  `IDmedicao` int(11) NOT NULL,
  `TipoSensor` varchar(3) NOT NULL,
  `DataHoraMedicao` datetime NOT NULL,
  `EmailUtilizadorConsultor` varchar(100) NOT NULL,
  `NomeUtilizadorConsultor` varchar(200) NOT NULL,
  `TipoUtilizadorConsultor` varchar(3) NOT NULL,
  `Data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Comando` varchar(200) NOT NULL,
  `Resultado` text,
  `PossivelAnomalia` tinyint(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estrutura da tabela `logronda`
--

CREATE TABLE `logronda` (
  `idlogRonda` int(11) NOT NULL,
  `DiaSemana` varchar(20) NOT NULL,
  `HoraRonda` time NOT NULL,
  `Duracao` int(11) NOT NULL,
  `EmailUtilizador` varchar(100) NOT NULL,
  `NomeUtilizador` varchar(200) NOT NULL,
  `TipoUtilizador` varchar(3) NOT NULL,
  `EmailUtilizadorConsultor` varchar(100) NOT NULL,
  `NomeUtilizadorConsultor` varchar(200) NOT NULL,
  `TipoUtilizadorConsultor` varchar(3) NOT NULL,
  `Data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Comando` varchar(200) NOT NULL,
  `Resultado` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estrutura da tabela `logrondaextra`
--

CREATE TABLE `logrondaextra` (
  `idlogRondaExtra` int(11) NOT NULL,
  `DataHora` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `EmailUtilizador` varchar(100) NOT NULL,
  `NomeUtilizador` varchar(200) NOT NULL,
  `TipoUtilizador` varchar(3) NOT NULL,
  `EmailUtilizadorConsultor` varchar(100) NOT NULL,
  `NomeUtilizadorConsultor` varchar(200) NOT NULL,
  `TipoUtilizadorConsultor` varchar(3) NOT NULL,
  `Data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Comando` varchar(200) NOT NULL,
  `Resultado` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estrutura da tabela `logsistema`
--

CREATE TABLE `logsistema` (
  `idlogSistema` int(11) NOT NULL,
  `IDSistema` int(11) NOT NULL,
  `LimiteTemperatura` decimal(6,2) NOT NULL,
  `LimiteHumidade` decimal(6,2) NOT NULL,
  `LimiteLuminosidade` decimal(6,2) NOT NULL,
  `LimiarTemperatura` decimal(6,2) NOT NULL,
  `LimiarHumidade` decimal(6,2) NOT NULL,
  `LimiarLuminosidade` decimal(6,2) NOT NULL,
  `DuracaoPadraoRonda` int(11) NOT NULL,
  `EmailUtilizadorConsultor` varchar(100) NOT NULL,
  `NomeUtilizadorConsultor` varchar(200) NOT NULL,
  `TipoUtilizadorConsultor` varchar(3) NOT NULL,
  `Data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Comando` varchar(200) NOT NULL,
  `Resultado` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estrutura da tabela `logutilizador`
--

CREATE TABLE `logutilizador` (
  `idlogUtilizador` int(11) NOT NULL,
  `EmailUtilizadorConsultado` varchar(100) NOT NULL,
  `NomeUtilizadorConsultado` varchar(200) NOT NULL,
  `TipoUtilizadorConsultado` varchar(3) NOT NULL,
  `EmailUtilizadorConsultor` varchar(100) NOT NULL,
  `NomeUtilizadorConsultor` varchar(200) NOT NULL,
  `TipoUtilizadorConsultor` varchar(3) NOT NULL,
  `Data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `Comando` varchar(200) NOT NULL,
  `Resultado` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `logalerta`
--
ALTER TABLE `logalerta`
  ADD PRIMARY KEY (`idlogAlerta`),
  ADD UNIQUE KEY `IDAlerta` (`IDAlerta`,`DataHoraAlerta`,`IDMedicao`,`DataHoraMedicao`,`Data`),
  ADD UNIQUE KEY `IDAlerta_2` (`IDAlerta`,`DataHoraAlerta`,`IDMedicao`,`DataHoraMedicao`,`Data`);

--
-- Indexes for table `logcartao`
--
ALTER TABLE `logcartao`
  ADD PRIMARY KEY (`idlogCartao`);

--
-- Indexes for table `logmedicaosensores`
--
ALTER TABLE `logmedicaosensores`
  ADD PRIMARY KEY (`idLogMedicao`),
  ADD UNIQUE KEY `IDmedicao` (`IDmedicao`,`Data`);

--
-- Indexes for table `logronda`
--
ALTER TABLE `logronda`
  ADD PRIMARY KEY (`idlogRonda`),
  ADD UNIQUE KEY `Data` (`Data`);

--
-- Indexes for table `logrondaextra`
--
ALTER TABLE `logrondaextra`
  ADD PRIMARY KEY (`idlogRondaExtra`),
  ADD UNIQUE KEY `DataHora` (`DataHora`,`Data`);

--
-- Indexes for table `logsistema`
--
ALTER TABLE `logsistema`
  ADD PRIMARY KEY (`idlogSistema`),
  ADD UNIQUE KEY `IDSistema` (`IDSistema`,`Data`);

--
-- Indexes for table `logutilizador`
--
ALTER TABLE `logutilizador`
  ADD PRIMARY KEY (`idlogUtilizador`),
  ADD UNIQUE KEY `Data` (`Data`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
