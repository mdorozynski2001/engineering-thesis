-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 17, 2023 at 02:00 PM
-- Wersja serwera: 10.4.28-MariaDB
-- Wersja PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `inventory_app`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `inventory`
--

CREATE TABLE `inventory` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `stock_id` int(11) NOT NULL,
  `date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `inventory_details`
--

CREATE TABLE `inventory_details` (
  `id` int(11) NOT NULL,
  `inventory_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `amount_start` int(11) NOT NULL,
  `amount_correct` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `products`
--

CREATE TABLE `products` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `barcode` bigint(11) NOT NULL,
  `total_amount` int(11) NOT NULL,
  `price` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `name`, `barcode`, `total_amount`, `price`) VALUES
(1, 'AquaFresh triple protection', 5908311868447, 6, 4.99),
(2, 'Camembert', 4337185526046, 9, 6.99),
(3, 'milk', 8718951313255, 16, 2.99),
(4, 'flour', 4056489805175, 6, 2.99),
(5, 'mandarynki', 20599973, 12, 4.99);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `stock`
--

CREATE TABLE `stock` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `type` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stock`
--

INSERT INTO `stock` (`id`, `name`, `type`) VALUES
(1, 'warehouse_1', 'warehouse'),
(2, 'warehouse_2', 'warehouse'),
(3, 'production_1', 'production'),
(4, 'subcontractor_1', 'subcontractor');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `stock_details`
--

CREATE TABLE `stock_details` (
  `id` int(11) NOT NULL,
  `stock_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `amount` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `stock_details`
--

INSERT INTO `stock_details` (`id`, `stock_id`, `product_id`, `amount`) VALUES
(1, 1, 1, 6),
(2, 1, 2, 5),
(3, 2, 2, 4),
(4, 2, 3, 8),
(5, 1, 5, 12),
(6, 3, 3, 8),
(7, 4, 4, 6);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `name` varchar(30) NOT NULL,
  `surname` varchar(30) NOT NULL,
  `username` varchar(30) NOT NULL,
  `password` varchar(32) NOT NULL,
  `is_active` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `name`, `surname`, `username`, `password`, `is_active`) VALUES
(2, 'Marcin', 'Dorożyński', 'marcin', 'd5fad0cda8f1079681ec510bb20a586c', 1);

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `inventory`
--
ALTER TABLE `inventory`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id_inventory` (`user_id`),
  ADD KEY `stock_id_inventory` (`stock_id`);

--
-- Indeksy dla tabeli `inventory_details`
--
ALTER TABLE `inventory_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `product_id_inventory_details` (`product_id`),
  ADD KEY `inventory_id_inventory_details` (`inventory_id`);

--
-- Indeksy dla tabeli `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `barcode` (`barcode`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indeksy dla tabeli `stock`
--
ALTER TABLE `stock`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `name` (`name`);

--
-- Indeksy dla tabeli `stock_details`
--
ALTER TABLE `stock_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `WH_detail_product_id` (`product_id`),
  ADD KEY `WH_detail_warehouse_id` (`stock_id`);

--
-- Indeksy dla tabeli `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `login` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `inventory`
--
ALTER TABLE `inventory`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `inventory_details`
--
ALTER TABLE `inventory_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `stock`
--
ALTER TABLE `stock`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `stock_details`
--
ALTER TABLE `stock_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `inventory`
--
ALTER TABLE `inventory`
  ADD CONSTRAINT `stock_id_inventory` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`),
  ADD CONSTRAINT `user_id_inventory` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `inventory_details`
--
ALTER TABLE `inventory_details`
  ADD CONSTRAINT `inventory_id_inventory_details` FOREIGN KEY (`inventory_id`) REFERENCES `inventory` (`id`),
  ADD CONSTRAINT `product_id_inventory_details` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

--
-- Constraints for table `stock_details`
--
ALTER TABLE `stock_details`
  ADD CONSTRAINT `WH_detail_product_id` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  ADD CONSTRAINT `WH_detail_warehouse_id` FOREIGN KEY (`stock_id`) REFERENCES `stock` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
