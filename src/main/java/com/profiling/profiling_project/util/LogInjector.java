package com.profiling.profiling_project.util;

import spoon.Launcher;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.code.CtCodeSnippetStatementImpl;

public class LogInjector {
    public static void main(String[] args) {
        // Initialisation de Spoon
        Launcher launcher = new Launcher();
        launcher.addInputResource("src/main/java/com/profiling/profiling_project/controller"); // Dossier des fichiers sources
        launcher.setSourceOutputDirectory("target/generated-sources"); // Dossier de sortie

        // Ajout du processus de transformation
        launcher.addProcessor(new spoon.processing.AbstractProcessor<CtMethod<?>>() {
            @Override
            public void process(CtMethod<?> method) {
                // Filtrer uniquement les méthodes publiques des contrôleurs
                CtClass<?> declaringClass = (CtClass<?>) method.getDeclaringType();
                if (declaringClass.getSimpleName().endsWith("Controller") && method.isPublic()) {
                    Factory factory = getFactory();

                    // Générer une ligne de log avec l'utilisateur authentifié
                    CtCodeSnippetStatement logStatement = new CtCodeSnippetStatementImpl();
                    logStatement.setValue(
                            "logger.info(\"Utilisateur: \" + SecurityContextHolder.getContext().getAuthentication().getName() + " +
                                    "\", Méthode appelée: " + method.getSimpleName() + "\")"
                    );

                    // Ajouter le log au début de la méthode
                    method.getBody().insertBegin(logStatement);
                }
            }
        });

        // Lancer le traitement
        launcher.run();
    }
}

