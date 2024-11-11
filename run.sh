#!/usr/bin/env bash

# Accept input flags -i, -o, and -m from  the user and set environment variables INPUT_DIRECTORY, OUTPUT_DIRECTORY and OUTPUT_MODE respectively
while getopts i:o:m: flag
do
    case "${flag}" in
        i) INPUT_DIRECTORY=${OPTARG};;
        o) OUTPUT_DIRECTORY=${OPTARG};;
        m) OUTPUT_MODE=${OPTARG};;
    esac
done

# If the user has not provided the -i or -o flags, print an error message explaining the usage and exit
if [ -z "$INPUT_DIRECTORY" ] || [ -z "$OUTPUT_DIRECTORY" ]; then
    echo "Usage: run.sh -i <input_directory> -o <output_directory> [-m <output_mode> (json, graphql or all)]"
    exit 1
fi

# Export the variables as environment variables
export INPUT_DIRECTORY
export OUTPUT_DIRECTORY
export OUTPUT_MODE

# Run the docker container via the docker-compose file and remove it afer it has finished running
docker-compose up --build --remove-orphans --abort-on-container-exit





