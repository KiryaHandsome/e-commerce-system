# E-commerce ordering system

[![Build and Test](https://github.com/KiryaHandsome/e-commerce-system/actions/workflows/ci.yaml/badge.svg)](https://github.com/KiryaHandsome/e-commerce-system/actions/workflows/ci.yaml)
[![CodeFactor](https://www.codefactor.io/repository/github/kiryahandsome/e-commerce-system/badge)](https://www.codefactor.io/repository/github/kiryahandsome/e-commerce-system)

## Services

1. **Order service**: service exposes REST api for ordering goods
2. **Payment service**: responsible for orders payments 
3. **Inventory service**: responsible for tracking goods at warehouse

## Services communication
Services must use Kafka to communication to each other