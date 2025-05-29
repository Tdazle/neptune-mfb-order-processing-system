#!/bin/bash
psql -U postgres -c "CREATE DATABASE orderdb;"
psql -U postgres -c "CREATE DATABASE inventorydb;"